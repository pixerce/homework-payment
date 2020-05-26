package com.example.mission.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class ApiErrorResponse<T> extends BaseModelSupport {

    private static final long serialVersionUID = -4929084816234791071L;

    private HttpStatus status;
    private String message;
    private T error;

}
