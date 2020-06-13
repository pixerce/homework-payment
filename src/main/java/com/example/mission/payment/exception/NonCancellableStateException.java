package com.example.mission.payment.exception;

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
