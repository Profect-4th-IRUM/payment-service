package com.irum.paymentservice.payment.client.dto;

public record TossPaymentsRequest(String paymentKey, String orderId, int amount) {}
