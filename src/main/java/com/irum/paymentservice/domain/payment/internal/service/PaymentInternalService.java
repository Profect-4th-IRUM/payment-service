package com.irum.paymentservice.domain.payment.internal.service;

import com.irum.global.advice.exception.CommonException;
import com.irum.openfeign.payment.dto.request.CreatePaymentRequest;
import com.irum.openfeign.payment.dto.request.UpdatePaymentStatusRequest;
import com.irum.openfeign.payment.dto.response.PaymentResponse;
import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentCorp;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentRepository;
import com.irum.paymentservice.domain.payment.internal.PaymentResponseMapper;
import com.irum.paymentservice.global.exception.errorcode.PaymentErrorCode;
import com.irum.paymentservice.global.util.MemberUtil;
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
    private final MemberUtil memberUtil;

    public UUID preparePayment(CreatePaymentRequest request) {
        Long memberId = memberUtil.getCurrentMemberId();

        Payment payment =
                Payment.builder()
                        .memberId(memberId)
                        .amount(request.finalPaymentAmount())
                        .totalDiscountAmount(request.discountAmount())
                        .paymentStatus(PaymentStatus.PENDING)
                        .paymentCorp(PaymentCorp.valueOf(request.paymentCorp().toString()))
                        .build();
        Payment savedPayment = paymentRepository.save(payment);
        return savedPayment.getPaymentId();
    }

    public PaymentResponse getPayment(UUID paymentId) {
        Payment payment =
                paymentRepository
                        .findById(paymentId)
                        .orElseThrow(() -> new CommonException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        return PaymentResponseMapper.toPaymentResponse(payment);
    }

    public int updatePaymentFailed(UpdatePaymentStatusRequest request) {
        return paymentRepository.updateStatusToFailedByIds(request.paymentIdList());
    }
}
