package com.irum.paymentservice.openfeign.order.dto.request;

import com.irum.paymentservice.openfeign.order.dto.enums.OrderStatus;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateOrderStatusFailedRequest(
        UUID orderId, UUID paymentId, OrderStatus orderStatus) {}
