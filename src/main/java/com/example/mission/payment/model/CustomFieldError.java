package com.example.mission.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomFieldError {

    private String field;
    private String value;
    private String reason;
}
