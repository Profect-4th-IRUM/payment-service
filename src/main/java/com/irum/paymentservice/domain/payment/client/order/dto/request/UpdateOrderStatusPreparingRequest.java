package com.irum.paymentservice.domain.payment.client.order.dto.request;

import com.irum.paymentservice.domain.payment.client.order.dto.enums.OrderStatus;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdateOrderStatusPreparingRequest(OrderStatus orderStatus, UUID orderId) {}
