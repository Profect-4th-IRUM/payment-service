package com.irum.paymentservice.openfeign.toss.dto;

public record TossPaymentsErrorResponse (
        String code,
        String message
    ) {

}
