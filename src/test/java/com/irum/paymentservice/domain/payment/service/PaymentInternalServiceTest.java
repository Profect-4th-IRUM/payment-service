package com.irum.paymentservice.domain.payment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.irum.global.advice.exception.CommonException;
import com.irum.openfeign.payment.dto.request.CreatePaymentRequest;
import com.irum.openfeign.payment.dto.request.UpdatePaymentStatusRequest;
import com.irum.openfeign.payment.dto.response.PaymentResponse;
import com.irum.openfeign.payment.emuns.PaymentCorp;
import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentMethod;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentRepository;
import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentInternalRequest;
import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentStatusUpdateRequest;
import com.irum.paymentservice.domain.payment.internal.dto.response.PaymentInternalResponse;
import com.irum.paymentservice.domain.payment.internal.service.PaymentInternalService;
import com.irum.paymentservice.global.exception.errorcode.PaymentErrorCode;
import com.irum.paymentservice.global.util.MemberUtil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentInternalServiceTest {

    @InjectMocks private PaymentInternalService paymentInternalService;
    @Mock private PaymentRepository paymentRepository;
    @Mock private MemberUtil memberUtil;

    @Test
    @DisplayName("preparePayment: 결제 준비(PENDING) 상태로 저장 성공")
    void preparePayment_Success() {
        // given
        CreatePaymentRequest request = new CreatePaymentRequest(10000, 2000, PaymentCorp.TOSS);
        Long currentMemberId = 123L;
        when(memberUtil.getCurrentMemberId()).thenReturn(currentMemberId);

        // save
        UUID newPaymentId = UUID.randomUUID();
        Payment savedPayment =
                Payment.builder()
                        .paymentId(newPaymentId)
                        .memberId(currentMemberId)
                        .amount(request.finalPaymentAmount())
                        .totalDiscountAmount(request.discountAmount())
                        .paymentStatus(PaymentStatus.PENDING)
                        .paymentCorp(com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentCorp.TOSS)
                        .build();
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // ArgumentCaptor: save에 실제로 어떤 객체가 전달되었는지 캡처
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        // when
        UUID returnedPaymentId = paymentInternalService.preparePayment(request);

        // then
        assertThat(returnedPaymentId).isEqualTo(newPaymentId);

        verify(memberUtil, times(1)).getCurrentMemberId();
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());

        // 저장한 Payment 필드가 올바르게 설정되었는지 확인
        Payment paymentToSave = paymentCaptor.getValue();
        assertThat(paymentToSave.getMemberId()).isEqualTo(currentMemberId);
        assertThat(paymentToSave.getAmount()).isEqualTo(10000);
        assertThat(paymentToSave.getTotalDiscountAmount()).isEqualTo(2000);
        assertThat(paymentToSave.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(paymentToSave.getPaymentCorp()).isEqualTo(com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentCorp.TOSS);
    }

    @Test
    @DisplayName("getPayment: 결제 정보 조회 성공")
    void getPayment_Success() {
        // given
        UUID paymentId = UUID.randomUUID();
        Payment foundPayment =
                Payment.builder()
                        .paymentId(paymentId)
                        .amount(15000)
                        .totalDiscountAmount(2000)
                        .paymentMethod(PaymentMethod.CARD)
                        .paymentStatus(PaymentStatus.PAID)
                        .build();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(foundPayment));

        // when
        PaymentResponse response = paymentInternalService.getPayment(paymentId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.paymentMethod().toString()).isEqualTo(foundPayment.getPaymentMethod().toString());
        assertThat(response.paymentStatus().toString()).isEqualTo(foundPayment.getPaymentStatus().toString());

        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    @DisplayName("getPayment: 결제 정보 조회 실패 (Payment Not Found)")
    void getPayment_Failure_NotFound() {
        // given
        UUID paymentId = UUID.randomUUID();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // when  then
        assertThatThrownBy(() -> paymentInternalService.getPayment(paymentId))
                .isInstanceOf(CommonException.class)
                .extracting("errorCode")
                .isEqualTo(PaymentErrorCode.PAYMENT_NOT_FOUND);

        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    @DisplayName("updatePaymentFailed: 결제 상태 FAILED로 업데이트 성공")
    void updatePaymentFailed_Success() {
        // given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UpdatePaymentStatusRequest request = new UpdatePaymentStatusRequest(List.of(id1, id2));

        int expectedUpdatedRows = 2;
        when(paymentRepository.updateStatusToFailedByIds(request.paymentIdList()))
                .thenReturn(expectedUpdatedRows);

        // when
        int actualUpdatedRows = paymentInternalService.updatePaymentFailed(request);

        // then
        assertThat(actualUpdatedRows).isEqualTo(expectedUpdatedRows);

        verify(paymentRepository, times(1)).updateStatusToFailedByIds(List.of(id1, id2));
    }
}
