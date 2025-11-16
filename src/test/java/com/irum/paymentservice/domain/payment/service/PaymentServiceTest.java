package com.irum.paymentservice.domain.payment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentMethod;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentRepository;
import com.irum.paymentservice.domain.payment.dto.request.PaymentRequest;
import com.irum.paymentservice.domain.payment.dto.response.PaymentResponse;
import com.irum.paymentservice.global.util.MemberUtil;
import com.irum.paymentservice.openfeign.order.OrderAPI;
import com.irum.paymentservice.openfeign.order.enums.OrderStatus;
import com.irum.paymentservice.openfeign.toss.TosspaymentsAPI;
import com.irum.paymentservice.openfeign.toss.dto.TossPaymentsResponse;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
	@InjectMocks PaymentService paymentService;

	@Mock TosspaymentsAPI tosspaymentsAPI;
	@Mock PaymentRepository paymentRepository;
	@Mock MemberUtil memberUtil;
	@Mock OrderAPI orderAPI;
	@Mock
	Payment payment;

	// --- 테스트용 공통 데이터 ---
	private PaymentRequest paymentRequest;
	private TossPaymentsResponse tossResponse;
	private final UUID TEST_PAYMENT_ID = UUID.randomUUID();
	private final UUID TEST_ORDER_ID = UUID.randomUUID();
	private final String TEST_ORDER_NUM = "ORD-0000000";
	private final Long TEST_MEMBER_ID = 123L;
	private final int TEST_AMOUNT = 10000;


	@BeforeEach
	void setUp() {
		// 테스트에서 공통으로 사용할 DTO 객체 초기화
		paymentRequest = new PaymentRequest("tossOrderId", TEST_ORDER_ID, "tossPaymentKey", TEST_PAYMENT_ID);
		tossResponse = new TossPaymentsResponse("tossPaymentKey", "tossOrderId", PaymentStatus.PAID.toString(), PaymentMethod.CARD.toString());
	}

	@Test
	@DisplayName("결제 승인 성공")
	void createPayment_Success() {
		// given
		when(paymentRepository.findById(TEST_PAYMENT_ID)).thenReturn(Optional.of(payment));
		// member 검증
		when(payment.getMemberId()).thenReturn(TEST_MEMBER_ID);
		doNothing().when(memberUtil).assertMemberResourceAccess(TEST_MEMBER_ID);

		when(payment.getPaymentStatus()).thenReturn(PaymentStatus.PENDING);
		when(payment.getAmount()).thenReturn(TEST_AMOUNT);

		// tosspaymentsAPI 호출
		when(tosspaymentsAPI.confirmPayment(paymentRequest, TEST_AMOUNT)).thenReturn(tossResponse);
		// orderAPI 호출 -> 주문 번호 반환
		when(orderAPI.updateOrderStatusPreparing(OrderStatus.PREPARING, TEST_ORDER_ID)).thenReturn(TEST_ORDER_NUM);

		// when
		PaymentResponse response = paymentService.createPayment(paymentRequest);

		// then
		assertThat(response).isNotNull();
		assertThat(response.orderNum()).isEqualTo(TEST_ORDER_NUM);
		assertThat(response.totalAmount()).isEqualTo(TEST_AMOUNT);

		verify(paymentRepository, times(1)).findById(TEST_PAYMENT_ID);
		verify(memberUtil, times(1)).assertMemberResourceAccess(TEST_MEMBER_ID);
		verify(tosspaymentsAPI, times(1)).confirmPayment(paymentRequest, TEST_AMOUNT);
		verify(payment, times(1)).updateToPaid(tossResponse);
		verify(orderAPI, times(1)).updateOrderStatusPreparing(OrderStatus.PREPARING, TEST_ORDER_ID);

		verify(payment, never()).updateStatus(PaymentStatus.FAILED);
		verify(orderAPI, never()).updateOrderStatusFailed(any(), any(), any());
	}

}
