package com.irum.paymentservice.domain.payment.client.order.dto.request;

import com.irum.paymentservice.domain.payment.client.order.dto.enums.OrderStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateOrderStatusFailedRequest(
        UUID orderId,
        UUID paymentId,
        OrderStatus orderStatus
) {
}
