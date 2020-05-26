package com.example.mission.payment.controller;

import com.example.mission.entity.Amount;
import com.example.mission.entity.Card;
import com.example.mission.entity.Payment;
import com.example.mission.payment.service.PaymentService;
import com.example.mission.payment.exception.InvalidStateRequestException;
import com.example.mission.payment.exception.NonCancellableStateException;
import com.example.mission.payment.model.*;
import com.example.mission.payment.validator.ApproveRequestValidator;
import com.example.mission.payment.validator.CancelRequestValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/payment")
public class PaymentController {

    final private PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping(value = "/approve", produces = "application/json;charset=UTF-8")
    public ApproveResponse approve(@RequestBody ApproveRequest request, BindingResult bindingResult) throws BindException {

        ValidationUtils.invokeValidator(new ApproveRequestValidator(), request, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        final String cardNumber = request.getCardNumber();
        final Payment payment = this.service.approve(cardNumber, request.getValidDate(), request.getCvc(), String.valueOf(request.getInstallment()), request.getAmount(), request.getVat());
        final Amount amount = payment.getAmount();

        return ApproveResponse.withSuccess()
                .tid(payment.getTid())
                .amount(amount.getAmount())
                .vat(amount.getVat())
                .approvedAt(payment.getUpdatedAt())
                .cardNumber(Card.masking(cardNumber))
                .build();
    }

    @PostMapping(value = "/cancel", produces = "application/json;charset=UTF-8")
    public CancelResponse cancel(@RequestBody CancelRequest request, BindingResult bindingResult) throws BindException, RuntimeException {

        ValidationUtils.invokeValidator(new CancelRequestValidator(), request, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        final Payment cancellation = this.service.cancel(request.getTid(), request.getAmount(), request.getVat());
        final Amount amount = cancellation.getAmount();
        return CancelResponse.withSuccess()
                    .amount(amount.getAmount())
                    .vat(amount.getVat())
                    .tid(cancellation.getTid())
                    .approvedAt(cancellation.getUpdatedAt())
                    .build();

    }

    @GetMapping(value = "/detail/{tid}", produces = "application/json;charset=UTF-8")
    public DetailResponse detail(@PathVariable("tid") String tid) throws IllegalArgumentException {

        if (StringUtils.isBlank(tid) || tid.length() != 20)
           throw new IllegalArgumentException(String.format("%s, tid: 20자리", tid));

        return this.service.detail(tid);
    }

    @ExceptionHandler({BindException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiErrorResponse> handle(Exception e) {

        List<CustomFieldError> fieldErrorList = new ArrayList<>();
        if (e instanceof IllegalArgumentException) {
            fieldErrorList.add(new CustomFieldError("tid", "", e.getMessage()));
        } else {
            BindException be = (BindException)e;
            for (FieldError error : be.getBindingResult().getFieldErrors()) {
                fieldErrorList.add(new CustomFieldError(error.getField(), error.getRejectedValue().toString(), error.getDefaultMessage()));
            }
        }
        HttpHeaders responseHeaders = new HttpHeaders(); responseHeaders.add("Content-Type", "application/json;charset=UTF-8");

        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.BAD_REQUEST, "검증 오류", fieldErrorList);
        ResponseEntity<ApiErrorResponse> entity = ResponseEntity.status(HttpStatus.OK)
                .headers(responseHeaders)
                .body(response);
        return entity;
    }

    @ExceptionHandler({NonCancellableStateException.class, InvalidStateRequestException.class})
    public ResponseEntity<ApiErrorResponse> handleNonCancellable(NonCancellableStateException e) {
        HttpHeaders responseHeaders = new HttpHeaders(); responseHeaders.add("Content-Type", "application/json;charset=UTF-8");
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getErrorCode());

        ResponseEntity<ApiErrorResponse> entity = ResponseEntity.status(HttpStatus.OK)
                .headers(responseHeaders)
                .body(response);
        return entity;

    }

}
