package com.irum.paymentservice.global.exception.business;

/** 보상 트랜잭션 제외 대상 (409, 422, 이미 처리된 결제)*/
public class PaymentAlreadyProcessedException extends RuntimeException {
    public PaymentAlreadyProcessedException(String message) {
        super(message);
    }
}
