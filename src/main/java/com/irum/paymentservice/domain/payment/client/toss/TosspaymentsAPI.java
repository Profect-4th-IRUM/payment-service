package com.irum.paymentservice.domain.payment.client.toss;

import com.irum.paymentservice.domain.payment.client.config.FeignConfig;
import com.irum.paymentservice.domain.payment.client.toss.dto.TossPaymentsRequest;
import com.irum.paymentservice.domain.payment.client.toss.dto.TossPaymentsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "TOSS",
        url = "https://api.tosspayments.com",
        configuration = FeignConfig.class)
public interface TosspaymentsAPI {
    @PostMapping("/v1/payments/confirm")
    TossPaymentsResponse confirmPayment(
            @RequestHeader("Authorization") String auth, @RequestBody TossPaymentsRequest request);
}
