package com.irum.paymentservice.domain.payment.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.paymentservice.domain.payment.dto.request.PaymentRequest;
import com.irum.paymentservice.domain.payment.dto.response.PaymentResponse;
import com.irum.paymentservice.domain.payment.service.PaymentService;
import com.irum.paymentservice.global.config.TestConfig;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureRestDocs
@Import({TestConfig.class})
@ActiveProfiles("test")
public class PaymentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private PaymentService paymentService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("결제 등록 API")
    void paymentCreateApiTest() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        int totalAmount = 10000;
        PaymentRequest request =
                new PaymentRequest("tossOrderId", orderId, "tosspaymentkey", paymentId);
        PaymentResponse response = new PaymentResponse(totalAmount);

        Mockito.when(paymentService.createPayment(request)).thenReturn(response);

        mockMvc.perform(
                        post("/payments")
                                .with(csrf())
                                .with(user("100").roles("CUSTOMER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.totalAmount").value(totalAmount))
                .andDo(
                        document(
                                "payment-create",
                                requestFields(
                                        fieldWithPath("tossOrderId")
                                                .description("토스에서 발급해주는 orderId"),
                                        fieldWithPath("tossPaymentKey")
                                                .description("토스에서 발급해주는 paymentKey"),
                                        fieldWithPath("orderId").description("결제하려는 주문 id"),
                                        fieldWithPath("paymentId").description("결제 id")),
                                responseFields(
                                        fieldWithPath("success").description("성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp")
                                                .description("응답 생성 시각 (ISO-8601)"),
                                        subsectionWithPath("data").description("응답 데이터 객체")),
                                responseFields(
                                        beneathPath("data").withSubsectionId("data"),
                                        fieldWithPath("totalAmount").description("실 결제 금액"))));
    }
}
