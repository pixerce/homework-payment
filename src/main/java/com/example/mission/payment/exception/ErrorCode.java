package com.example.mission.payment.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    NonCancellable(-1, Category.CANCEL, "취소 실패"),
    NotAcceptablePrice(-2, Category.CANCEL, "취소 금액 오류"),

    DuplicateRequest(-3, Category.COMMON, "중복 요청 발생");

    private int code;
    private Category category;
    private String message;

    ErrorCode(int code, Category category, String message) {
        this.code = code;
        this.category = category;
        this.message = message;
    }

    enum Category {
        COMMON("공통"), CANCEL("취소 실패"), PAY("결제 실패");

        private String msg;

        Category(String msg) {
            this.msg = msg;
        }

        public String getCategory() {
            return this.msg;
        }
    }

    public String getCategory() {
        return this.category.getCategory();
    }

    public String getDetail() {
        return this.message;
    }

    public int getCode() {
        return this.code;
    }
}
