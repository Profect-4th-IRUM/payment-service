package com.irum.paymentservice.openfeign.toss.dto;

public record TossPaymentsResponse(
        String paymentKey, // 결제 조회, 결제 취소에 사용 되기 때문에 필수 저장
        String orderId, // 결제 조회, 결제 취소에 사용 되기 때문에 필수 저장
        String status, // 결제 상태
        String method // 결제 수단
        ) {}
