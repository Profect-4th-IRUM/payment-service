package com.irum.paymentservice.global.exception.business;

import feign.Request;
import feign.RetryableException;

/** 재시도 대상인 에러일때 (5xx, timeout등) */
public class PaymentTossServerException extends RetryableException {
    public PaymentTossServerException(int status, String message, Request request) {
        super(status, message, request.httpMethod(), (Long) null, request);
    }
}
