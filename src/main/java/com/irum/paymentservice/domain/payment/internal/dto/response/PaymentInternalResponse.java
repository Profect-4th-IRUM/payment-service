package com.irum.paymentservice.domain.payment.internal.dto.response;

import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentMethod;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import lombok.Builder;

@Builder
public record PaymentInternalResponse(
        PaymentStatus paymentStatus,
        PaymentMethod paymentMethod,
        int totalDiscountAmount,
        int amount) {
    public static PaymentInternalResponse from(Payment p) {
        return PaymentInternalResponse.builder()
                .paymentStatus(p.getPaymentStatus())
                .paymentMethod(p.getPaymentMethod())
                .totalDiscountAmount(p.getTotalDiscountAmount())
                .amount(p.getAmount())
                .build();
    }
}
