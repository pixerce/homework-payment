package com.example.mission.payment.exception;

import com.fasterxml.jackson.annotation.JsonValue;

public class NonCancellableStateException extends RuntimeException {

    private ErrorCode code;

    public NonCancellableStateException(ErrorCode code) {
        this.code = code;
    }

    public ErrorCode getErrorCode() {
        return this.code;
    }

    public String getCategory() {
        return this.code.getCategory();
    }

}
