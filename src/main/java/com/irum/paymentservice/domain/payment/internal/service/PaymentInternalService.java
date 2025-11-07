package com.irum.paymentservice.domain.payment.internal.service;

import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentRepository;
import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentInternalRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
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
}
