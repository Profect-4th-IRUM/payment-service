package com.irum.paymentservice.domain.payment.internal.controller;

import com.irum.paymentservice.domain.payment.dto.request.PaymentRequest;
import com.irum.paymentservice.domain.payment.dto.response.PaymentResponse;
import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentInternalRequest;
import com.irum.paymentservice.domain.payment.internal.service.PaymentInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/payment/")
public class PaymentInternalController {
    private final PaymentInternalService paymentInternalService;

    @PatchMapping("failed")
    public int updatePaymentFailed(@RequestBody PaymentInternalRequest request) {
        return paymentInternalService.updatePaymentFailed(request);
    }
}
