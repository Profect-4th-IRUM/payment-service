package com.irum.paymentservice.domain.payment.internal.dto.request;

import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentCorp;

public record PaymentInternalRequest(
        int finalPaymentAmount, int discountAmount, PaymentCorp paymentCorp) {}
