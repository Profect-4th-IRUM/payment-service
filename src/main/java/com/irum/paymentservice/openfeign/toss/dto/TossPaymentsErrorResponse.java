package com.irum.paymentservice.openfeign.toss.dto;

public record TossPaymentsErrorResponse (
        String version,
        String traceId,
        ErrorDetail errorDetail
){
    public record ErrorDetail (
            String code,
            String message
    ) {
    }
}
