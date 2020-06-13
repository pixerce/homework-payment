package com.example.mission.payment;

import com.example.mission.payment.repository.entity.Payment;
import com.example.mission.payment.exception.NonCancellableStateException;
import com.example.mission.payment.model.CancelRequest;
import com.example.mission.payment.repository.PaymentRepository;
import com.example.mission.payment.service.PaymentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class MissionTests {

    @Autowired
    private PaymentService service;

    @Autowired
    private PaymentRepository repository;

    @Test
    @Transactional
    void test_부분취소_Case1() {
        Payment response = this.service.approve("1234123412346789", "0520", "393", "0", 11_000L, 1_000L);
        String tid = response.getTid();

        CancelRequest r1 = new CancelRequest(tid, 1100, 100L);
        CancelRequest r2 = new CancelRequest(tid, 3300, null);
        CancelRequest r3 = new CancelRequest(tid, 7000, null);
        CancelRequest r4 = new CancelRequest(tid, 6600, 700L);
        CancelRequest r5 = new CancelRequest(tid, 6600, 600L);
        CancelRequest r6 = new CancelRequest(tid, 100, null);

        this.service.cancel(r1.getTid(), r1.getAmount(), r1.getVat());
        this.service.cancel(r2.getTid(), r2.getAmount(), r2.getVat());
        Assertions.assertThrows(NonCancellableStateException.class, () -> this.service.cancel(r3.getTid(), r3.getAmount(), r3.getVat()));
        Assertions.assertThrows(NonCancellableStateException.class, () -> this.service.cancel(r4.getTid(), r4.getAmount(), r4.getVat()));
        this.service.cancel(r5.getTid(), r5.getAmount(), r5.getVat());
        Assertions.assertThrows(NonCancellableStateException.class, () -> this.service.cancel(r6.getTid(), r6.getAmount(), r6.getVat()));
    }

    @Test
    @Transactional
    void test_부분취소_Case2() {
        Payment response = this.service.approve("0123412341234678", "0520", "393", "0", 20_000L, 909L);
        String tid = response.getTid();

        CancelRequest r1 = new CancelRequest(tid, 10_000, 909L);
        CancelRequest r2 = new CancelRequest(tid, 10_000, 909L);
        CancelRequest r3 = new CancelRequest(tid, 10_000, 0L);

        this.service.cancel(r1.getTid(), r1.getAmount(), r1.getVat());
        Assertions.assertThrows(NonCancellableStateException.class, () -> this.service.cancel(r2.getTid(), r2.getAmount(), r2.getVat()));
        this.service.cancel(r3.getTid(), r3.getAmount(), r3.getVat());

    }

    @Test
    @Transactional
    void test_부분취소_Case3() {
        Payment response = this.service.approve("4012341234123467", "0520", "393", "0", 20_000L, null);
        String tid = response.getTid();

        CancelRequest r1 = new CancelRequest(tid, 10_000, 1_000L);
        CancelRequest r2 = new CancelRequest(tid, 10_000, 909L);
        CancelRequest r3 = new CancelRequest(tid, 10_000, null);

        this.service.cancel(r1.getTid(), r1.getAmount(), r1.getVat());
        Assertions.assertThrows(NonCancellableStateException.class, () -> this.service.cancel(r2.getTid(), r2.getAmount(), r2.getVat()));
        this.service.cancel(r3.getTid(), r3.getAmount(), r3.getVat());

    }
}
