package com.irum.paymentservice.domain.payment.internal.service;

import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentCorp;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentRepository;
import com.irum.paymentservice.domain.payment.dto.request.PaymentRequest;
import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentInternalRequest;
import com.irum.paymentservice.domain.payment.openfeign.member.MemberClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentInternalService {
    private final PaymentRepository paymentRepository;
    private final MemberClient memberClient;


    public UUID preparePayment(
            PaymentInternalRequest request) {
        UUID memberId = memberClient.getMemberId();

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
