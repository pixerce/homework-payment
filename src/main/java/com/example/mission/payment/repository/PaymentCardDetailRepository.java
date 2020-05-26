package com.example.mission.payment.repository;

import com.example.mission.entity.PaymentCardDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PaymentCardDetailRepository extends JpaRepository<PaymentCardDetail, Long> {

    @Transactional(readOnly = true)
    @Query("SELECT paymentCardDetail FROM PaymentCardDetail paymentCardDetail WHERE paymentCardDetail.tid =:tid")
    List<PaymentCardDetail> findByTid(@Param("tid")String tid);
}
