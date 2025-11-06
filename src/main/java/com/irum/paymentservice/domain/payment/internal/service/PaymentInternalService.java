package com.irum.paymentservice.domain.payment.internal.service;

import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentRepository;
import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentStatusUpdateRequest;
import com.irum.paymentservice.domain.payment.internal.dto.response.PaymentInternalResponse;
import com.irum.paymentservice.global.presentation.advice.exception.CommonException;
import com.irum.paymentservice.global.presentation.advice.exception.errorcode.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentInternalService {
    private final PaymentRepository paymentRepository;

    public PaymentInternalResponse getPayment(UUID paymentId){
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new CommonException(PaymentErrorCode.PAYMENT_NOT_FOUND)
        );
        return PaymentInternalResponse.from(payment);
    }
    public int updatePaymentFailed(PaymentStatusUpdateRequest request) {
        return paymentRepository.updateStatusToFailedByIds(request.paymetIdList());
    }
}
