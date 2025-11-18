package com.irum.paymentservice.domain.payment.service;

import com.irum.global.advice.exception.CommonException;
import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentRepository;
import com.irum.paymentservice.global.exception.errorcode.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentStatusService {
    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW) // 더티체킹을 위한 새로운 트랜잭션
    public void updatePaymentStatusFailed(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CommonException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        payment.updateStatus(PaymentStatus.FAILED);
    }
}