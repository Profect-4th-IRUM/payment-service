package com.irum.paymentservice.domain.payment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.irum.global.advice.exception.CommonException;
import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentMethod;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentRepository;
import com.irum.paymentservice.domain.payment.dto.request.PaymentRequest;
import com.irum.paymentservice.domain.payment.dto.response.PaymentResponse;
import com.irum.paymentservice.global.exception.errorcode.PaymentErrorCode;
import com.irum.paymentservice.global.util.MemberUtil;
import com.irum.paymentservice.openfeign.order.OrderAPI;
import com.irum.paymentservice.openfeign.order.enums.OrderStatus;
import com.irum.paymentservice.openfeign.toss.TosspaymentsAPI;
import com.irum.paymentservice.openfeign.toss.dto.TossPaymentsResponse;
import feign.FeignException;
import feign.Request;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @InjectMocks PaymentService paymentService;

    @Mock TosspaymentsAPI tosspaymentsAPI;
    @Mock PaymentRepository paymentRepository;
    @Mock MemberUtil memberUtil;
    @Mock OrderAPI orderAPI;
    @Mock Payment payment;

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
        paymentRequest =
                new PaymentRequest("tossOrderId", TEST_ORDER_ID, "tossPaymentKey", TEST_PAYMENT_ID);
        tossResponse =
                new TossPaymentsResponse(
                        "tossPaymentKey",
                        "tossOrderId",
                        PaymentStatus.PAID.toString(),
                        PaymentMethod.CARD.toString());
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
        // orderAPI 호출
        when(orderAPI.updateOrderStatusPreparing(OrderStatus.PREPARING, TEST_ORDER_ID))
                .thenReturn(TEST_ORDER_NUM);

        // when
        PaymentResponse response = paymentService.createPayment(paymentRequest);

        // then
        // 반환값 확인
        assertThat(response).isNotNull();
        assertThat(response.orderNum()).isEqualTo(TEST_ORDER_NUM);
        assertThat(response.totalAmount()).isEqualTo(TEST_AMOUNT);

        // 한번 호출
        verify(paymentRepository, times(1)).findById(TEST_PAYMENT_ID);
        verify(memberUtil, times(1)).assertMemberResourceAccess(TEST_MEMBER_ID);
        verify(tosspaymentsAPI, times(1)).confirmPayment(paymentRequest, TEST_AMOUNT);
        verify(payment, times(1)).updateToPaid(tossResponse);
        verify(orderAPI, times(1)).updateOrderStatusPreparing(OrderStatus.PREPARING, TEST_ORDER_ID);

        // 실패 로직 호출 x
        verify(payment, never()).updateStatus(PaymentStatus.FAILED);
        verify(orderAPI, never()).updateOrderStatusFailed(any(), any(), any());
    }

    @Test
    @DisplayName("결제 승인 실패 - 존재하지 않는 결제 ID")
    void createPayment_Failure_PaymentNotFound() {
        // given
        when(paymentRepository.findById(TEST_PAYMENT_ID)).thenReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> paymentService.createPayment(paymentRequest))
                .isInstanceOf(CommonException.class)
                .extracting("errorCode")
                .isEqualTo(PaymentErrorCode.PAYMENT_NOT_FOUND);

        verify(memberUtil, never()).assertMemberResourceAccess(anyLong());
        verify(tosspaymentsAPI, never()).confirmPayment(any(), anyInt());
        verify(orderAPI, never()).updateOrderStatusPreparing(any(), any());
    }

    @Test
    @DisplayName("결제 승인 실패 - 이미 처리된 결제")
    void createPayment_Failure_AlreadyProcessed() {
        // given
        when(paymentRepository.findById(TEST_PAYMENT_ID)).thenReturn(Optional.of(payment));
        when(payment.getMemberId()).thenReturn(TEST_MEMBER_ID);
        doNothing().when(memberUtil).assertMemberResourceAccess(TEST_MEMBER_ID);
        // 이미 처리된 결제
        when(payment.getPaymentStatus()).thenReturn(PaymentStatus.PAID);

        // when then
        assertThatThrownBy(() -> paymentService.createPayment(paymentRequest))
                .isInstanceOf(CommonException.class)
                .extracting("errorCode")
                .isEqualTo(PaymentErrorCode.PAYMENT_BAD_REQUEST);

        verify(tosspaymentsAPI, never()).confirmPayment(any(), anyInt());
        verify(orderAPI, never()).updateOrderStatusPreparing(any(), any());
    }

    @Test
    @DisplayName("결제 승인 실패 - toss api 호출 실패")
    void createPayment_Failure_TossAPIFailed() {
        // given
        when(paymentRepository.findById(TEST_PAYMENT_ID)).thenReturn(Optional.of(payment));
        // member 검증
        when(payment.getMemberId()).thenReturn(TEST_MEMBER_ID);
        doNothing().when(memberUtil).assertMemberResourceAccess(TEST_MEMBER_ID);

        when(payment.getPaymentStatus()).thenReturn(PaymentStatus.PENDING);
        when(payment.getAmount()).thenReturn(TEST_AMOUNT);

        // toss 호출 에러
        Request dummyRequest =
                Request.create(
                        Request.HttpMethod.POST,
                        "http://mock-toss-api/confirm",
                        Collections.emptyMap(),
                        (byte[]) null,
                        null);
        when(tosspaymentsAPI.confirmPayment(paymentRequest, TEST_AMOUNT))
                .thenThrow(
                        new FeignException.InternalServerError("결제실패", dummyRequest, null, null));

        // when then
        assertThatThrownBy(() -> paymentService.createPayment(paymentRequest))
                .isInstanceOf(CommonException.class)
                .extracting("errorCode")
                .isEqualTo(PaymentErrorCode.PAYMENT_ERROR);

        // then
        verify(tosspaymentsAPI, times(1)).confirmPayment(paymentRequest, TEST_AMOUNT);
        // 실패 로직 1번씩 호출
        verify(payment, times(1)).updateStatus(PaymentStatus.FAILED);
        verify(orderAPI, times(1))
                .updateOrderStatusFailed(OrderStatus.FAILED, TEST_ORDER_ID, TEST_PAYMENT_ID);
        // 성공 로직 호출 x
        verify(payment, never()).updateToPaid(any());
        verify(orderAPI, never()).updateOrderStatusPreparing(any(), any());
    }
}
