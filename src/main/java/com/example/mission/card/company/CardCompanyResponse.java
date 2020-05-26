package com.example.mission.card.company;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CardCompanyResponse {
    private String tid;
    private LocalDateTime approvedAt;

    public CardCompanyResponse(String tid) {
        this.tid = tid;
        this.approvedAt = LocalDateTime.now();
    }
}
