package com.irum.paymentservice.domain.payment.controller;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.openfeign.payment.dto.request.CreatePaymentRequest;
import com.irum.openfeign.payment.dto.request.UpdatePaymentStatusRequest;
import com.irum.openfeign.payment.dto.response.PaymentResponse;
import com.irum.openfeign.payment.emuns.PaymentCorp;
import com.irum.openfeign.payment.emuns.PaymentMethod;
import com.irum.openfeign.payment.emuns.PaymentStatus;
import com.irum.paymentservice.domain.payment.internal.controller.PaymentInternalController;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {PaymentInternalController.class})
@AutoConfigureRestDocs
@ActiveProfiles("test")
@Import({TestConfig.class})
public class PaymentInternalControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private PaymentInternalService paymentInternalService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("결제 정보 조회 API")
    void getPaymentAPITest() throws Exception {
        // given
        UUID paymentId = UUID.randomUUID();
        PaymentResponse response = new PaymentResponse(PaymentStatus.PAID, PaymentMethod.CARD);
        when(paymentInternalService.getPayment(paymentId)).thenReturn(response);

        // when then
        mockMvc.perform(
                        get("/internal/payments/{paymentId}", paymentId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentMethod").value(response.paymentMethod().toString()))
                .andExpect(jsonPath("$.paymentStatus").value(response.paymentStatus().toString()))
                .andDo(
                        document(
                                "payment-get",
                                pathParameters(
                                        parameterWithName("paymentId").description("조회할 결제 ID")),
                                responseFields(
                                        fieldWithPath("paymentMethod").description("결제 수단"),
                                        fieldWithPath("paymentStatus")
                                                .description("결제 상태")
                                                .description("할인 금액"))));
    }

    @Test
    @DisplayName("결제 상태 Failed 업데이트")
    void updatePaymentFailedAPITest() throws Exception {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UpdatePaymentStatusRequest request = new UpdatePaymentStatusRequest(List.of(id1, id2));
        when(paymentInternalService.updatePaymentFailed(request)).thenReturn(2);

        // when then
        mockMvc.perform(
                        put("/internal/payments/failed")
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
        CreatePaymentRequest request = new CreatePaymentRequest(10000, 2000, PaymentCorp.TOSS);
        when(paymentInternalService.preparePayment(any(CreatePaymentRequest.class)))
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
