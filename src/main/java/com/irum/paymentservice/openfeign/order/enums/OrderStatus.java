package com.irum.paymentservice.openfeign.order.enums;

public enum OrderStatus {
    FAILED,
    PENDING,
    PREPARING,
    PARTIALLY_SHIPPED,
    SHIPPED,
    PARTIALLY_DELIVERED,
    DELIVERED
}
