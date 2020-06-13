package com.example.mission.payment;

import com.example.mission.payment.repository.entity.*;
import com.example.mission.payment.repository.entity.type.Status;
import com.example.mission.payment.controller.PaymentController;
import com.example.mission.payment.exception.ErrorCode;
import com.example.mission.payment.exception.NonCancellableStateException;
import com.example.mission.payment.model.ApproveRequest;
import com.example.mission.payment.model.CancelRequest;
import com.example.mission.payment.model.DetailResponse;
import com.example.mission.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService service;

    final long amount = 11_000;
    final String cardNumber = "1234567890123456";
    final String cvc = "101";
    final String installment = "0";
    final String validThru = "0820";
    final String tid = "T1234567890123456789";
    final Long vat = null;

    ApproveRequest approveRequest = null;
    CancelRequest cancelRequest = null;

    @BeforeEach
    void setup() {
        approveRequest = ApproveRequest.builder()
                .amount(amount)
                .cardNumber(cardNumber)
                .cvc(cvc)
                .validDate(validThru)
                .build();

        cancelRequest = new CancelRequest(tid, amount, null);
    }

    @Test
    void testApproveSuccess() throws Exception {

        Payment payment = Payment.payWith(tid, Amount.of(amount, vat), Status.DN, new Card());

        given(this.service.approve(cardNumber, validThru, cvc, installment, amount, vat)).willReturn(payment);

        mockMvc.perform(post("/payment/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(approveRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.tid").exists())
                .andExpect(jsonPath("$.vat").exists())
                .andExpect(jsonPath("$.approvedAt").exists())
                .andExpect(jsonPath("$.cardNumber").exists())
                .andExpect(jsonPath("$.amount").exists());
    }

    @Test
    void testCancelSuccess() throws Exception {

        Payment payment = Payment.cancelWith(tid, Amount.of(amount, vat), Status.DN, new Card());

        given(this.service.cancel(tid, amount, null)).willReturn(payment);

        mockMvc.perform(post("/payment/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(cancelRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.tid").exists())
                .andExpect(jsonPath("$.approvedAt").exists())
                .andExpect(jsonPath("$.amount").exists())
                .andExpect(jsonPath("$.vat").exists());
    }

    @Test
    void testDetailSuccess() throws Exception {

        DetailResponse response = DetailResponse.builder()
                .amount(amount).vat(amount / 11)
                .approvedAt(LocalDateTime.now())
                .latestCanceledAt(LocalDateTime.now())
                .canceledAmount(amount).canceledVat(amount/11)
                .tid(tid)
                .status(DetailResponse.Status.CANCELLED)
                .cardNumber(cardNumber).cvc(cvc).validDate(validThru)
                .build();

        given(this.service.detail(tid)).willReturn(response);

        mockMvc.perform(get("/payment/detail/" + tid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.tid").exists())
                .andExpect(jsonPath("$.amount").exists())
                .andExpect(jsonPath("$.vat").exists())
                .andExpect(jsonPath("$.canceledAmount").exists())
                .andExpect(jsonPath("$.canceledVat").exists())
                .andExpect(jsonPath("$.approvedAt").exists())
                .andExpect(jsonPath("$.latestCanceledAt").exists())
                .andExpect(jsonPath("$.cardNumber").exists())
                .andExpect(jsonPath("$.cvc").exists())
                .andExpect(jsonPath("$.validDate").exists());
    }

    @Test
    void testApproveReturnFailResponse() throws Exception {
        this.approveRequest.setCardNumber(cardNumber + "alpha");

        mockMvc.perform(post("/payment/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(approveRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.error").exists());

    }

    @Test
    void testCancelReturnFailResponse() throws Exception {

        given(this.service.cancel(tid, cancelRequest.getAmount(), cancelRequest.getVat()))
                .willThrow(new NonCancellableStateException(ErrorCode.NotAcceptablePrice));

        mockMvc.perform(post("/payment/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(cancelRequest)))
                .andDo(print())
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.status").exists())
//                .andExpect(jsonPath("$.message").exists())
//                .andExpect(jsonPath("$.error").exists());

    }

    @Test
    void testDetailReturnFailResponse() throws Exception {

        mockMvc.perform(get("/payment/detail/" + tid + "alpha"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.error").exists());

    }


}
