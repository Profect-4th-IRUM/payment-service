package com.irum.paymentservice.openfeign.order;

import com.irum.paymentservice.openfeign.order.client.OrderClient;
import com.irum.paymentservice.openfeign.order.enums.OrderStatus;
import com.irum.paymentservice.openfeign.order.dto.request.UpdateOrderStatusFailedRequest;
import com.irum.paymentservice.openfeign.order.dto.request.UpdateOrderStatusPreparingRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderAPI {
    private final OrderClient orderAPI;

    public String updateOrderStatusPreparing(OrderStatus orderStatus, UUID orderId) {
        UpdateOrderStatusPreparingRequest request =
                UpdateOrderStatusPreparingRequest.builder()
                        .orderId(orderId)
                        .orderStatus(orderStatus)
                        .build();
        return orderAPI.updateOrderStatusPreparing(request);
    }

    public void updateOrderStatusFailed(OrderStatus orderStatus, UUID orderId, UUID paymentId) {
        UpdateOrderStatusFailedRequest request =
                UpdateOrderStatusFailedRequest.builder()
                        .orderId(orderId)
                        .orderStatus(orderStatus)
                        .paymentId(paymentId)
                        .build();

        orderAPI.updateOrderStatusFailed(request);
    }
}
