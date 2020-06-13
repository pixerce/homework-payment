package com.example.mission.payment.repository.entity;

import com.example.mission.payment.repository.entity.type.Status;
import com.example.mission.payment.repository.entity.type.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "payment")
public class Payment extends BaseEntity {

    @Column(name = "tid")
    private String tid;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;

    @AttributeOverrides({
            @AttributeOverride(name="amount", column = @Column(name="amount")),
            @AttributeOverride(name="vat", column = @Column(name="vat"))
    })
    @Embedded
    private Amount amount;


    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(name = "payment_card", joinColumns = @JoinColumn(name = "paymentId", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name = "cardId", referencedColumnName="id"))
    private Card card;

    public static Payment payWith(String tid, Amount amount, Status status, Card card) {
        return new Payment(tid, Type.PAYMENT, amount, status, LocalDateTime.now(), LocalDateTime.now(), card);
    }

    public static Payment cancelWith(String tid, Amount amount, Status status, Card card) {
        return new Payment(tid, Type.CANCEL, amount, status, LocalDateTime.now(), LocalDateTime.now(), card);
    }



}
