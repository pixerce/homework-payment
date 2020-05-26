package com.example.mission.payment.validator;


import com.example.mission.payment.model.CancelRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CancelRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return CancelRequest.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        CancelRequest request = (CancelRequest)o;

        long amount = request.getAmount();
        if ( amount < 100 && 1_000_000_000 < amount)
            errors.reject("amount", "금액: 100원 이상, 10억 이하");

        Long vat = request.getVat();
        if (vat != null && (amount < vat || vat < 0))
            errors.reject("vat", "부가세: 빈 값 혹은 0원 이상 결제 금액 이하");

        String tid = request.getTid();
        if (StringUtils.isBlank(tid) || tid.length() != 20)
            errors.reject("tid", "관리번호: 20자리");

    }
}
