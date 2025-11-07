package com.irum.paymentservice.openfeign.order.dto.enums;

public enum OrderStatus {
    FAILED,
    PENDING,
    PREPARING,
    PARTIALLY_SHIPPED,
    SHIPPED,
    PARTIALLY_DELIVERED,
    DELIVERED
}
