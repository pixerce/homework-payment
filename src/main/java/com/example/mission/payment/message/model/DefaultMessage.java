package com.example.mission.payment.message.model;


import com.example.mission.payment.message.annotation.DigitField;
import com.example.mission.payment.message.annotation.EncryptField;
import com.example.mission.payment.message.annotation.Order;
import com.example.mission.payment.message.annotation.TextField;
import com.example.mission.payment.message.type.Padding;
import com.example.mission.payment.model.BaseModelSupport;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DefaultMessage extends BaseModelSupport implements Message {

    @Order(0)
    @TextField(length = 10)
    private String type;

    @Order(1)
    @TextField(length = 20)
    private String tid;

    @Order(2)
    @DigitField(length = 20, padding = Padding.RIGHT_EMPTY)
    private String cardNumber;

    @Order(3)
    @DigitField(length = 2, padding = Padding.LEFT_ZERO)
    private String installment;

    @Order(4)
    @DigitField(length = 4, padding = Padding.RIGHT_EMPTY)
    private String validDate;

    @Order(5)
    @DigitField(length = 3, padding = Padding.RIGHT_EMPTY)
    private String cvc;

    @Order(6)
    @DigitField(length=10)
    private long amount;

    @Order(7)
    @DigitField(length = 10, padding = Padding.LEFT_ZERO)
    private long vat;

    @Order(8)
    @TextField(length = 20)
    private String payTid; // 취소 시에

    @EncryptField
    @Order(9)
    @TextField(length = 300)
    private String cardInfo;

    @Order(10)
    @TextField(length = 47)
    private String extra;

    /*public static DefaultMessage of(Payment payment, String installment) {
        Amount amount = payment.getAmount();
        Card card = payment.getCard();
        return DefaultMessage.builder()
                .amount(amount.getAmount())
                .vat(amount.getVat())
                .cardInfo(card.getInfo())
                .cardNumber(card.getNumber())
                .cvc(card.getCvc())
                .installment(installment)
                .tid(payment.getTid())
                .type(payment.getType().name())
                .validDate(card.getValidDate())
                .build();
    }
*/
}
