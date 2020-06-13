package com.example.mission.payment.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment_card_detail")
public class PaymentCardDetail extends BaseEntity {
    @Column(name="tid")
    private String tid;

    @Column(name="cardTid")
    private String cardTid;

    @Column(name="installment")
    private String installment;

    @Column(name="approved_at")
    private LocalDateTime approvedAt;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getInstallment() {
        return installment;
    }

    public void setInstallment(String installment) {
        this.installment = installment;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getCardTid() {
        return cardTid;
    }

    public void setCardTid(String cardTid) {
        this.cardTid = cardTid;
    }

}
