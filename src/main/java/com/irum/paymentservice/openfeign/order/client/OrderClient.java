// package com.irum.paymentservice.openfeign.order.client;
//
// import com.irum.paymentservice.openfeign.order.dto.request.UpdateOrderStatusFailedRequest;
// import com.irum.paymentservice.openfeign.order.dto.request.UpdateOrderStatusPreparingRequest;
// import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.web.bind.annotation.PatchMapping;
// import org.springframework.web.bind.annotation.RequestBody;
//
// @FeignClient(
//        name = "ORDER-SERVICE2",
//        url = "/internal/orders/"
// )
// public interface OrderClient {
//
//    @PatchMapping("preparing")
//    String updateOrderStatusPreparing(@RequestBody UpdateOrderStatusPreparingRequest request);
//
//    @PatchMapping("failed")
//    void updateOrderStatusFailed(
//            @RequestBody UpdateOrderStatusFailedRequest updateOrderStatusFailed);
// }
