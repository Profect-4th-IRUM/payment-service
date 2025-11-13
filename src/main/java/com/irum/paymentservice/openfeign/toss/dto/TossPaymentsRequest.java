package com.irum.paymentservice.openfeign.toss.dto;

public record TossPaymentsRequest(String paymentKey, String orderId, int amount) {}
