package com.example.mission.payment.service;

import com.example.mission.card.company.CardCompanyResponse;
import com.example.mission.card.company.Client;
import com.example.mission.entity.*;
import com.example.mission.entity.type.Status;
import com.example.mission.entity.type.Type;
import com.example.mission.payment.crypto.CipherHandler;
import com.example.mission.payment.exception.ErrorCode;
import com.example.mission.payment.exception.NonCancellableStateException;
import com.example.mission.payment.message.model.DefaultMessage;
import com.example.mission.payment.model.CardInfo;
import com.example.mission.payment.model.DetailResponse;
import com.example.mission.payment.repository.CardRepository;
import com.example.mission.payment.repository.PaymentCardDetailRepository;
import com.example.mission.payment.repository.PaymentRepository;
import com.example.mission.payment.sync.DisallowConcurrentRequest;
import com.example.mission.payment.sync.HashingTarget;
import com.example.mission.payment.utils.TidGenerator;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.mission.entity.Card.hash;

@Slf4j
@Service
public class PaymentService {

    private PaymentRepository paymentRepository;

    private CardRepository cardRepository;

    private Client client;

    private CipherHandler cipher;

    private PaymentCardDetailRepository detailRepository;

    private final static String CANCEL_INSTALLMENT = "00";

    public PaymentService(PaymentRepository paymentRepository, CardRepository cardRepository, PaymentCardDetailRepository detailRepository, Client client, CipherHandler cipher) {
        this.paymentRepository = paymentRepository;
        this.client = client;
        this.cipher = cipher;
        this.cardRepository = cardRepository;
        this.detailRepository = detailRepository;
    }

    final private Card getCard(String cardNumber, String validDate, String cvc) {
        final String cardInfo = Arrays.asList(cardNumber, validDate, cvc).stream().collect(Collectors.joining("|"));
        final String hashKey = hash(cardInfo);

        final String encryptedCardInfo = this.cipher.encrypt(cardInfo);
        return Card.builder().hashKey(hashKey).info(encryptedCardInfo).build();
    }

    @DisallowConcurrentRequest
    public Payment approve(@HashingTarget final String cardNumber, String validDate, final String cvc, String installment, @HashingTarget long payAmount, Long vat) {

        final Card card = this.getCard(cardNumber, validDate, cvc);
        final Amount amount = Amount.of(payAmount, vat);
        Payment payment = Payment.payWith(TidGenerator.generate(), amount, Status.WT, card);

        this.paymentRepository.saveAndFlush(payment);

        DefaultMessage message = DefaultMessage.builder()
                .amount(amount.getAmount())
                .vat(amount.getVat())
                .cardInfo(card.getInfo())
                .cardNumber(cardNumber)
                .cvc(cvc)
                .installment(installment)
                .tid(payment.getTid())
                .type(payment.getType().name())
                .validDate(validDate)
                .build();
        // 카드사와 통신
        CardCompanyResponse response = this.client.exchange(message);

        try {
            PaymentCardDetail detail = new PaymentCardDetail(payment.getTid(), response.getTid(), installment, response.getApprovedAt());
            this.detailRepository.save(detail);

            payment.setStatus(Status.DN);
        } catch (RuntimeException e) {

            message.setPayTid(payment.getTid());
            message.setType(Type.CANCEL.name());
            response = this.client.exchange(message);

            this.detailRepository.save(new PaymentCardDetail(payment.getTid(), response.getTid(), installment, response.getApprovedAt()));

            payment.setStatus(Status.FL);
        }
        payment.setUpdatedAt(LocalDateTime.now());
        return this.paymentRepository.saveAndFlush(payment);
    }

    @DisallowConcurrentRequest
    public Payment cancel(@HashingTarget final String tid, @HashingTarget long cancelAmount, Long vat) {

        List<Payment> paymentHistory = this.paymentRepository.findByTid(tid);
        long remain = Optional.ofNullable(paymentHistory).orElse(new ArrayList<>()).stream()
                .mapToLong(t -> Type.PAYMENT == t.getType() ? t.getAmount().getAmount() : -t.getAmount().getAmount())
                .sum();

        long remainVat = Optional.ofNullable(paymentHistory).orElse(new ArrayList<>()).stream()
                .mapToLong(t -> Type.PAYMENT == t.getType() ? t.getAmount().getVat() : -t.getAmount().getVat())
                .sum();

        final Amount amount = Amount.of(cancelAmount, Optional.ofNullable(vat).orElse(remain == cancelAmount ? remainVat : null));
        Payment cancellation = Payment.cancelWith(tid, amount, Status.WT, Card.NONE);

        if (amount.getAmount() > remain || cancellation.getAmount().getVat() > remainVat) {
            cancellation.setStatus(Status.FL);
            cancellation.setUpdatedAt(LocalDateTime.now());
            this.paymentRepository.save(cancellation);

            throw new NonCancellableStateException(ErrorCode.NotAcceptablePrice);

        } else {
            if (remain == amount.getAmount())
                cancellation.getAmount().setVat(remainVat);
            this.paymentRepository.save(cancellation);
        }

        final Payment pay = paymentHistory.stream().filter(p -> Type.PAYMENT == p.getType()).findAny().get();
        final CardInfo card = CardInfo.with(cipher.decrypt(pay.getCard().getInfo()));

        DefaultMessage message = DefaultMessage.builder()
                .amount(amount.getAmount())
                .vat(amount.getVat())
                .cardInfo(pay.getCard().getInfo())
                .cardNumber(card.getCardNumber())
                .cvc(card.getCvc())
                .installment(CANCEL_INSTALLMENT)
                .tid(tid)
                .payTid(tid)
                .type(Type.CANCEL.name())
                .validDate(card.getValidDate())
                .build();

        CardCompanyResponse response = this.client.exchange(message);

        this.detailRepository.save(new PaymentCardDetail(tid, response.getTid(), CANCEL_INSTALLMENT, response.getApprovedAt()));

        cancellation.setStatus(Status.DN);
        cancellation.setUpdatedAt(LocalDateTime.now());

        return this.paymentRepository.saveAndFlush(cancellation);
    }

    @Cacheable(cacheNames = "payment")
    public DetailResponse detail(final String tid) {
        List<Payment> payments = this.paymentRepository.findAllByTid(tid);

        final Payment pay = payments.stream().filter(p -> Type.PAYMENT == p.getType()).findAny().get();

        List<Payment> cancelList = payments.stream().filter(p -> Type.CANCEL == p.getType())
                .sorted(Comparator.comparing(Payment::getUpdatedAt, Comparator.reverseOrder())).collect(Collectors.toList());

        Long canceledAmount = ObjectUtils.isEmpty(cancelList) ? null : cancelList.stream().map(Payment::getAmount).mapToLong(Amount::getAmount).sum();
        Long canceledVat = ObjectUtils.isEmpty(cancelList) ? null : cancelList.stream().map(Payment::getAmount).mapToLong(Amount::getVat).sum();
        LocalDateTime latestCanceledAt = ObjectUtils.isEmpty(cancelList) ? null : cancelList.get(0).getUpdatedAt();

        String decrypted = cipher.decrypt(pay.getCard().getInfo());
        String [] info = StringUtils.split(decrypted, "|");

        DetailResponse.Status status = pay.getStatus() == Status.DN ? DetailResponse.Status.PAY_SUCCESS: DetailResponse.Status.PAY_FAILURE;
        if (!ObjectUtils.isEmpty(cancelList)) {
            status = (pay.getAmount().getAmount() == canceledAmount) ? DetailResponse.Status.CANCELLED : DetailResponse.Status.PARTIAL_CANCELLED;
        }

        return DetailResponse.builder()
                .status(status)
                .amount(pay.getAmount().getAmount())
                .vat(pay.getAmount().getVat())
                .approvedAt(pay.getUpdatedAt())
                .canceledAmount(canceledAmount)
                .canceledVat(canceledVat)
                .latestCanceledAt(latestCanceledAt)
                .cardNumber(Card.masking(info[0]))
                .validDate(info[1])
                .cvc(info[2])
                .build();
    }

}
