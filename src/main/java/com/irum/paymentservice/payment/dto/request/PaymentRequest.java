package com.irum.paymentservice.payment.dto.request;

import java.util.UUID;

public record PaymentRequest(String tossOrderId, String tossPaymentKey, UUID orderId) {}
