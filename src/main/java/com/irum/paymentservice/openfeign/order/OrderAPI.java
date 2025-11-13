package com.irum.paymentservice.openfeign.order;

import com.irum.paymentservice.openfeign.config.FeignConfig;
import com.irum.paymentservice.openfeign.order.dto.request.UpdateOrderStatusFailedRequest;
import com.irum.paymentservice.openfeign.order.dto.request.UpdateOrderStatusPreparingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "ORDER-SERVICE",
        url = "order-service/internal/orders/",
        configuration = FeignConfig.class)
public interface OrderAPI {

    @PatchMapping("preparing")
    String updateOrderStatusPreparing(@RequestBody UpdateOrderStatusPreparingRequest request);

    @PatchMapping("failed")
    void updateOrderStatusFailed(
            @RequestBody UpdateOrderStatusFailedRequest updateOrderStatusFailed);
}
