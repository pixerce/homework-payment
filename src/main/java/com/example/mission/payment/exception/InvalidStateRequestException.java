package com.example.mission.payment.exception;

public class InvalidStateRequestException extends RuntimeException {

    private ErrorCode code;

    public InvalidStateRequestException(ErrorCode code) {
        this.code = code;
    }

    public ErrorCode getErrorCode() {
        return this.code;
    }

    public String getCategory() {
        return this.code.getCategory();
    }

}
