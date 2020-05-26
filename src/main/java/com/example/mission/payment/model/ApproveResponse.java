package com.example.mission.payment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApproveResponse extends BaseModelSupport {

    private String status;

    private String tid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime approvedAt;

    private long amount;
    private Long vat;

    private String cardNumber;

    public static ApproveResponseBuilder withSuccess() {
        return new ApproveResponseBuilder().status("SUCCESS");
    }
}
