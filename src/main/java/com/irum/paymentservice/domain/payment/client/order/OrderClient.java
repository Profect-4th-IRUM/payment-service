package com.irum.paymentservice.domain.payment.client.order;

import com.irum.paymentservice.domain.payment.client.order.dto.enums.OrderStatus;
import com.irum.paymentservice.domain.payment.client.order.dto.request.UpdateOrderStatusFailedRequest;
import com.irum.paymentservice.domain.payment.client.order.dto.request.UpdateOrderStatusPreparingRequest;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentRepository;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderClient {
    private final OrderAPI orderAPI;

    public String updateOrderStatusPreparing(OrderStatus orderStatus, UUID orderId) {
        UpdateOrderStatusPreparingRequest request = UpdateOrderStatusPreparingRequest.builder()
                .orderId(orderId)
                .orderStatus(orderStatus)
                .build();
        return orderAPI.updateOrderStatusPreparing(request);
    }

    public void updateOrderStatusFailed(OrderStatus orderStatus, UUID orderId, UUID paymentId) {
        UpdateOrderStatusFailedRequest request = UpdateOrderStatusFailedRequest.builder()
                .orderId(orderId)
                .orderStatus(orderStatus)
                .paymentId(paymentId)
                .build();

        orderAPI.updateOrderStatusFailed(request);
    }
}
