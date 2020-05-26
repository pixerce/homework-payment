package com.example.mission.payment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
public class CancelResponse extends BaseModelSupport {

    private String status;
    private String tid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime approvedAt;

    private long amount;
    private long vat;

    public static CancelResponse.CancelResponseBuilder withSuccess() {
        return new CancelResponse.CancelResponseBuilder().status("SUCCESS");
    }
}
