package com.example.mission.payment.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class DetailResponse extends BaseModelSupport {
    private String tid;
    private Status status;  // 결제 완료, 취소, 부분 취소

    private long amount;
    private long vat;

    private Long canceledAmount;
    private Long canceledVat;

    private LocalDateTime approvedAt;
    private LocalDateTime latestCanceledAt;

    private String cardNumber;
    private String cvc;
    private String validDate;


    public enum Status {
        PAY_SUCCESS("결제 성공"), PAY_FAILURE("결제 실패"), CANCELLED("전체 취소"), PARTIAL_CANCELLED("부분 취소");

        private String status;

        Status(String status) {
            this.status = status;
        }

        @JsonValue
        public String getName() {
            return status;
        }
    }
}
