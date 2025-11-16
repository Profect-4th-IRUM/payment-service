package com.irum.paymentservice.domain.payment.controller;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.global.advice.exception.GlobalExceptionHandler;
import com.irum.global.advice.response.CommonResponseAdvice;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentMethod;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.paymentservice.domain.payment.internal.controller.PaymentInternalController;
import com.irum.paymentservice.domain.payment.internal.dto.response.PaymentInternalResponse;
import com.irum.paymentservice.domain.payment.internal.service.PaymentInternalService;
import com.irum.paymentservice.global.config.TestConfig;

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
		//given
		UUID paymentId = UUID.randomUUID();
		PaymentInternalResponse response = PaymentInternalResponse.builder()
			.paymentMethod(PaymentMethod.CARD)
			.paymentStatus(PaymentStatus.PAID)
			.amount(10000)
			.totalDiscountAmount(2000)
			.build();
		when(paymentInternalService.getPayment(paymentId)).thenReturn(response);

		//when then
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
						fieldWithPath("totalDiscountAmount").description("할인 금액"))));

	}
}
