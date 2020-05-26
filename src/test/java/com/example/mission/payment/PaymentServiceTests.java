package com.example.mission.payment;

import com.example.mission.card.company.CardComponeyRepository;
import com.example.mission.entity.Card;
import com.example.mission.entity.Payment;
import com.example.mission.entity.PaymentCardDetail;
import com.example.mission.entity.type.Status;
import com.example.mission.entity.type.Type;
import com.example.mission.payment.exception.NonCancellableStateException;
import com.example.mission.payment.repository.CardRepository;
import com.example.mission.payment.repository.PaymentCardDetailRepository;
import com.example.mission.payment.repository.PaymentRepository;
import com.example.mission.payment.service.PaymentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(includeFilters = @ComponentScan.Filter({Service.class, Configuration.class}))
public class PaymentServiceTests {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private PaymentService service;

    @Autowired
    private CardComponeyRepository cardComponeyRepository;

    @Autowired
    private PaymentCardDetailRepository detailRepository;

    @Autowired
    private CacheManager cacheManager;

    String cardNumber = "1234432112344321";
    String validDate = "0720";
    String cvc = "909";
    String installment = "1";
    long amount = 110000;
    Long vat = 10000L;

    @Test
    void test_TID의_거래내역_조회() {
        List<Payment> payments = this.repository.findByTid("T0000000000000000001");
        assertThat(payments.size() == 2);
    }

    @Test
    @Transactional
    void test_결제_성공() {
        Payment payment = this.service.approve(cardNumber, validDate, cvc, installment, amount, vat);

        List<Payment> payments = this.repository.findByTid(payment.getTid());
        assertThat(payments.size() == 1);

        Card card = this.cardRepository.findByHashKey(payment.getCard().getHashKey());
        assertThat(card != null);

        List<PaymentCardDetail> paymentCardDetailList = this.detailRepository.findByTid(payment.getTid());
        assertThat(paymentCardDetailList.size() == 1);
    }

    @Test
    @Transactional
    void test_amount로_vat_계산() {
        Long nullVat = null;
        Payment payment = this.service.approve(cardNumber, validDate, cvc, installment, amount, nullVat);

        assertEquals(vat, payment.getAmount().getVat());
    }

    @Test
    @Transactional
    void test_전체_취소_성공() {
        Payment payment = this.service.cancel("T0000000000000000002", 11000, 1000L);

        assertEquals(Status.DN, payment.getStatus());
        List<PaymentCardDetail> paymentCardDetailList = this.detailRepository.findByTid(payment.getTid());
        assertThat(paymentCardDetailList.size() == 2);

        List<Payment> payments = this.repository.findByTid(payment.getTid());
        long remain = payments.stream().mapToLong(t -> t.getType() == Type.PAYMENT ? t.getAmount().getAmount() : -t.getAmount().getAmount()).sum();
        assertEquals(0, remain);
    }

    @Test
    @Transactional
    void test_결제금액_보다_큰_취소_금액_때문에_전체_취소_실패() {

        final String tid = "T0000000000000000002";
        Assertions.assertThrows(NonCancellableStateException.class, () -> {
                    this.service.cancel(tid, 12000, null);
                });
        List<PaymentCardDetail> paymentCardDetailList = this.detailRepository.findByTid(tid);
        assertThat(paymentCardDetailList.size() == 1);

        List<Payment> payments = this.repository.findByTid(tid);
        long remain = payments.stream().mapToLong(t -> t.getType() == Type.PAYMENT ? t.getAmount().getAmount() : -t.getAmount().getAmount()).sum();
        assertEquals(11000, remain);
    }

    @Test
    @Transactional
    void test_부분_취소_성공() {
        Payment payment = this.service.cancel("T0000000000000000002", 5000, null);

        assertEquals(Status.DN, payment.getStatus());
        List<PaymentCardDetail> paymentCardDetailList = this.detailRepository.findByTid(payment.getTid());
        assertThat(paymentCardDetailList.size() == 2);

        List<Payment> payments = this.repository.findByTid(payment.getTid());
        long remain = payments.stream().mapToLong(t -> t.getType() == Type.PAYMENT ? t.getAmount().getAmount() : -t.getAmount().getAmount()).sum();
        assertEquals(11000 - 5000, remain);
    }

    @Test
    void test_결제_내역에만_카드_정보_있음() {

        Payment pay = this.service.approve(cardNumber, validDate, cvc, installment, amount, vat);
        this.service.cancel(pay.getTid(), amount / 2, null);

        List<Payment> payments = this.repository.findByTid(pay.getTid());
        assertEquals(1, payments.stream().filter(p-> p.getCard() != null).map(Payment::getCard).count());
    }
}
