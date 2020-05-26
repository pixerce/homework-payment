package com.example.mission.payment.repository;

import com.example.mission.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Transactional(readOnly = true)
    @Query("SELECT payment FROM Payment payment WHERE payment.tid =:tid AND status = 'DN'")
    List<Payment> findByTid(@Param("tid")String tid);

    @Transactional(readOnly = true)
    @Query("SELECT payment FROM Payment payment WHERE payment.tid =:tid and (payment.type = 'PAYMENT' or (payment.type = 'CANCEL'))")
    List<Payment> findAllByTid(@Param("tid")String tid);
}
