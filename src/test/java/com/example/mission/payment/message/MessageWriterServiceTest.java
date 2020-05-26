package com.example.mission.payment.message;

import com.example.mission.entity.type.Type;
import com.example.mission.payment.message.model.DefaultMessage;
import com.example.mission.payment.message.model.Message;
import com.example.mission.payment.message.service.MessageWriterService;
import com.example.mission.payment.utils.TidGenerator;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MessageWriterServiceTest {

    @Autowired
    MessageWriterService service;

    String message;

    final String cardNumber = "1234567890123456";
    final String installment = "1";
    final String validDate = "0520";
    final String cvc = "234";
    final long amount = 11000;
    final String cardInfo = "YYYYYYYYYYYYYYYYYYYYYYYYYY";
    final long vat = 100;

    @BeforeEach
    void setup() {

        Message message = DefaultMessage.builder()
                .type(Type.PAYMENT.name())
                .tid(TidGenerator.generate())
                .cardNumber(cardNumber)
                .installment(installment)
                .validDate(validDate)
                .cvc(cvc)
                .amount(amount)
                .vat(vat)
                .cardInfo(cardInfo)
                .build();
        this.message = service.writeAsString(message);
    }

    @Test
    void validateData() {
        String content = StringUtils.substring(message, 4);
        assertEquals(cardNumber, StringUtils.trim(StringUtils.substring(content, 30, 50)));
        assertEquals(installment, StringUtils.removeStart(StringUtils.substring(content, 50, 52), "0"));
        assertEquals(cvc, StringUtils.removeEnd(StringUtils.substring(content, 56, 59), " "));
        assertEquals(amount, Integer.parseInt(StringUtils.trim(StringUtils.substring(content, 59, 69))));
    }

    @Test
    void shouldMessageLengthBe450() {
        assertEquals(450, message.length());
    }
}
