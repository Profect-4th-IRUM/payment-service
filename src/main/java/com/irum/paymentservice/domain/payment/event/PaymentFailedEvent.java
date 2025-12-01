package com.irum.paymentservice.domain.payment.event;

import com.irum.openfeign.order.enums.OrderStatus;
import java.util.UUID;

public record PaymentFailedEvent(UUID orderId, UUID paymentId, OrderStatus orderStatus) {
    public static PaymentFailedEvent from(UUID orderId, UUID paymentId, OrderStatus orderStatus) {
        return new PaymentFailedEvent(orderId, paymentId, orderStatus);
    }
}
