package com.irum.paymentservice.domain.payment.openfeign.toss.dto;

public record TossPaymentsRequest(String paymentKey, String orderId, int amount) {}
