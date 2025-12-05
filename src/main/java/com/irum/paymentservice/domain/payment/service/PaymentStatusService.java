package com.irum.paymentservice.domain.payment.service;

import com.irum.global.advice.exception.CommonException;
import com.irum.openfeign.order.enums.OrderStatus;
import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentRepository;
import com.irum.paymentservice.domain.payment.dto.request.PaymentRequest;
import com.irum.paymentservice.domain.payment.event.PaymentFailedEvent;
import com.irum.paymentservice.domain.payment.event.PaymentFailedOutboxEvent;
import com.irum.paymentservice.global.exception.errorcode.PaymentErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentStatusService {
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW) // 더티체킹을 위한 새로운 트랜잭션
    public void updatePaymentStatusFailed(UUID paymentId, PaymentRequest request) {
        Payment payment =
                paymentRepository
                        .findById(paymentId)
                        .orElseThrow(() -> new CommonException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        payment.updateStatus(PaymentStatus.FAILED);

        PaymentFailedEvent event = PaymentFailedEvent.from(request.orderId(), request.paymentId(), OrderStatus.FAILED);
        eventPublisher.publishEvent(new PaymentFailedOutboxEvent(payment.getPaymentId(), event));
        log.info("[내부] PaymentFailedOutboxEvent 발행 완료");
    }
}
