package com.irum.paymentservice.domain.payment.client.toss.dto;

public record TossPaymentsRequest(String paymentKey, String orderId, int amount) {}
