package com.irum.paymentservice.domain.payment.client.dto;

public record TossPaymentsRequest(String paymentKey, String orderId, int amount) {}
