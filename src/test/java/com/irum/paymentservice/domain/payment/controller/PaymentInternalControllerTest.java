package com.irum.paymentservice.domain.payment.controller;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.global.advice.exception.GlobalExceptionHandler;
import com.irum.global.advice.response.CommonResponseAdvice;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentCorp;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentMethod;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.paymentservice.domain.payment.internal.controller.PaymentInternalController;
import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentInternalRequest;
import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentStatusUpdateRequest;
import com.irum.paymentservice.domain.payment.internal.dto.response.PaymentInternalResponse;
import com.irum.paymentservice.domain.payment.internal.service.PaymentInternalService;
import com.irum.paymentservice.global.config.TestConfig;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {PaymentInternalController.class})
@AutoConfigureRestDocs
@Import({CommonResponseAdvice.class, GlobalExceptionHandler.class, TestConfig.class})
public class PaymentInternalControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private PaymentInternalService paymentInternalService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("결제 정보 조회 API")
    void getPaymentAPITest() throws Exception {
        // given
        UUID paymentId = UUID.randomUUID();
        PaymentInternalResponse response =
                PaymentInternalResponse.builder()
                        .paymentMethod(PaymentMethod.CARD)
                        .paymentStatus(PaymentStatus.PAID)
                        .amount(10000)
                        .totalDiscountAmount(2000)
                        .build();
        when(paymentInternalService.getPayment(paymentId)).thenReturn(response);

        // when then
        mockMvc.perform(
                        get("/internal/payments/{paymentId}", paymentId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentMethod").value(response.paymentMethod().toString()))
                .andExpect(jsonPath("$.paymentStatus").value(response.paymentStatus().toString()))
                .andExpect(jsonPath("$.amount").value(response.amount()))
                .andExpect(jsonPath("$.totalDiscountAmount").value(response.totalDiscountAmount()))
                .andDo(
                        document(
                                "payment-get",
                                pathParameters(
                                        parameterWithName("paymentId").description("조회할 결제 ID")),
                                responseFields(
                                        fieldWithPath("paymentMethod").description("결제 수단"),
                                        fieldWithPath("paymentStatus").description("결제 상태"),
                                        fieldWithPath("amount").description("결제 금액"),
                                        fieldWithPath("totalDiscountAmount")
                                                .description("할인 금액"))));
    }

    @Test
    @DisplayName("결제 상태 Failed 업데이트")
    void updatePaymentFailedAPITest() throws Exception {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest(List.of(id1, id2));
        when(paymentInternalService.updatePaymentFailed(request)).thenReturn(2);

        // when then
        mockMvc.perform(
                        patch("/internal/payments/failed")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$").value(2))
                .andDo(
                        document(
                                "payment-update-failed",
                                requestFields(
                                        fieldWithPath("paymentIdList")
                                                .description("상태 업데이트 할 결제 ID")),
                                responseBody()));
    }

    @Test
    @DisplayName("결제 준비 (pending상태)")
    void preparePaymentAPITest() throws Exception {
        // given
        UUID paymentId = UUID.randomUUID();
        PaymentInternalRequest request = new PaymentInternalRequest(10000, 2000, PaymentCorp.TOSS);
        when(paymentInternalService.preparePayment(any(PaymentInternalRequest.class)))
                .thenReturn(paymentId);

        // when then
        mockMvc.perform(
                        post("/internal/payments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$").value(paymentId.toString()))
                .andDo(
                        document(
                                "payment-prepare",
                                requestFields(
                                        fieldWithPath("finalPaymentAmount").description("실 결제금액"),
                                        fieldWithPath("discountAmount").description("할인금액"),
                                        fieldWithPath("paymentCorp").description("결제 회사")),
                                responseBody()));
    }
}
