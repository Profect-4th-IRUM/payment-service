package com.irum.paymentservice.domain.payment.event;

import com.irum.openfeign.order.enums.OrderStatus;
import lombok.Generated;

import java.util.UUID;

public record PaymentPaidEvent (
        OrderStatus orderStatus,
        UUID orderId
){
    public static PaymentPaidEvent from(OrderStatus orderStatus, UUID orderId){
        return new PaymentPaidEvent(orderStatus, orderId);
    }
}
