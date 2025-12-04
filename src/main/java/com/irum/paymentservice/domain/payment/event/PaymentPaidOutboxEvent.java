package com.irum.paymentservice.domain.payment.event;

import com.irum.paymentservice.domain.payment.domain.entity.Payment;

import java.util.UUID;

public record PaymentPaidOutboxEvent (
        UUID paymentId, // 상태가 변경된 엔티티
        PaymentPaidEvent payload
) {
}
