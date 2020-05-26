package com.example.mission.payment;

import com.example.mission.entity.type.Type;
import com.example.mission.payment.repository.PaymentRepository;
import com.example.mission.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(includeFilters = @ComponentScan.Filter({Service.class, Component.class, Configuration.class}))
public class MissionAdditionalTests {

    @Autowired
    private PaymentService service;

    @Autowired
    private PaymentRepository repository;

    String cardNumbr = "1234123412346789";
    String validDate = "0520";
    String cvc = "393";
    String installment = "0";
    long payAmount = 31_000L;
    long vat = 1_000L;

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @Transactional
    public void test_중복결제_방지() throws InterruptedException {

        final long count = repository.count();

        CountDownLatch latch = new CountDownLatch(1);
        Thread.setDefaultUncaughtExceptionHandler((Thread th, Throwable ex) ->
            latch.countDown()
        );

        this.service.approve(cardNumbr, validDate, cvc, installment, payAmount, vat);
        Thread.sleep(100);
        new Thread(() -> this.service.approve(cardNumbr, validDate, cvc, installment, payAmount, vat)).start();

        latch.await();
        assertEquals(count + 1, repository.count());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @Transactional
    public void test_중복결제_다른_금액은_허용() throws InterruptedException {

        final long count = repository.count();
        CountDownLatch latch = new CountDownLatch(1);

        this.service.approve(cardNumbr, validDate, cvc, installment, payAmount, vat);
        new Thread(() -> {
            this.service.approve(cardNumbr, validDate, cvc, installment, 20_000, vat);
            latch.countDown();
        }).start();

        latch.await();
        assertEquals(count + 2, repository.count());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    @Transactional
    public void test_중복취소_방지() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        Thread.setDefaultUncaughtExceptionHandler((Thread th, Throwable ex) ->
                latch.countDown()
        );

        final String cancelTid = "T0000000000000000002";
        this.service.cancel(cancelTid, 11000, 1000L);
        new Thread(() -> this.service.cancel(cancelTid, 11000, 1000L)).start();

        latch.await();
        long amount = this.repository.findByTid(cancelTid).stream().mapToLong(t->t.getType() == Type.PAYMENT ? t.getAmount().getAmount() : -t.getAmount().getAmount()).sum();
        assertEquals(0, amount);

    }

}
