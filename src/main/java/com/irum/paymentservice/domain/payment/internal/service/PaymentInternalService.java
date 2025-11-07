package com.irum.paymentservice.domain.payment.internal.service;

import com.irum.global.advice.exception.CommonException;
import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentRepository;
import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentInternalRequest;
import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentStatusUpdateRequest;
import com.irum.paymentservice.domain.payment.internal.dto.response.PaymentInternalResponse;
import com.irum.paymentservice.global.exception.errorcode.PaymentErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentInternalService {
    private final PaymentRepository paymentRepository;

    public UUID preparePayment(PaymentInternalRequest request) {
        Long memberId = 1L;

        Payment payment =
                Payment.builder()
                        .memberId(memberId)
                        .amount(request.finalPaymentAmount())
                        .totalDiscountAmount(request.discountAmount())
                        .paymentStatus(PaymentStatus.PENDING)
                        .paymentCorp(request.paymentCorp())
                        .build();
        Payment savedPayment = paymentRepository.save(payment);
        return savedPayment.getPaymentId();
    }

    public PaymentInternalResponse getPayment(UUID paymentId) {
        Payment payment =
                paymentRepository
                        .findById(paymentId)
                        .orElseThrow(() -> new CommonException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        return PaymentInternalResponse.from(payment);
    }

    public int updatePaymentFailed(PaymentStatusUpdateRequest request) {
        return paymentRepository.updateStatusToFailedByIds(request.paymetIdList());
    }
}
