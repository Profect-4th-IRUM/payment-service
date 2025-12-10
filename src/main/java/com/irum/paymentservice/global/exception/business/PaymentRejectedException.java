package com.irum.paymentservice.global.exception.business;

/** 보상 트랜잭션 대상 (4xx - 잔액 부족, 한도 초과 등) */
public class PaymentRejectedException extends RuntimeException {
    public PaymentRejectedException(String message) {
        super(message);
    }
}
