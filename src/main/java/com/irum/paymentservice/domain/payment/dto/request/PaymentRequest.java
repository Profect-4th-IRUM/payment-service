package com.irum.paymentservice.domain.payment.dto.request;

import java.util.UUID;

public record PaymentRequest(
        String tossOrderId, UUID orderId, String tossPaymentKey, UUID paymentId) {}
