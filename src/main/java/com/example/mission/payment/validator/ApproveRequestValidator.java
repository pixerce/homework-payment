package com.example.mission.payment.validator;

import com.example.mission.payment.model.ApproveRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ApproveRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return ApproveRequest.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ApproveRequest approve = (ApproveRequest)o;

        String number = approve.getCardNumber();
        if (!StringUtils.isNumeric(number) || (number.length() < 10 || number.length() > 16))
            errors.rejectValue("cardNumber", number, "카드 번호: 10 ~ 16자리의 숫자");

        long amount = approve.getAmount();
        if ( amount < 100 && 1_000_000_000 < amount)
            errors.reject("amount", "금액: 100원 이상, 10억 이하");

        Long vat = approve.getVat();
        if (vat != null && (amount < vat || vat < 0))
            errors.reject("vat", "부가세: 빈 값 혹은 0원 이상 결제 금액 이하");

        int installment = approve.getInstallment();
        if (installment < 0 || 12 < installment)
            errors.reject("installment", "할부 개월: 0(일시불), 1 ~ 12");

        String cvc = approve.getCvc();
        if (!StringUtils.isNumeric(cvc))
            errors.reject("cvc", "cvc: 3자리의 숫자");

        String validDate = approve.getValidDate();

        try {
            YearMonth date = YearMonth.parse(validDate, DateTimeFormatter.ofPattern("MMyy"));
            if (date.isBefore(YearMonth.now()))
                errors.reject("validDate", "카드 유효기간: 유효기간이 지난 카드는 사용할 수 없습니다.");

        } catch (DateTimeParseException e) {
            errors.reject("validDate", "카드 유효기간: 월년의 4자리 숫자 예) 0520");
        }
    }
}
