package com.irum.paymentservice.domain.payment.internal.controller;

import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentInternalRequest;
import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentStatusUpdateRequest;
import com.irum.paymentservice.domain.payment.internal.dto.response.PaymentInternalResponse;
import com.irum.paymentservice.domain.payment.internal.service.PaymentInternalService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/payments/")
public class PaymentInternalController {
    private final PaymentInternalService paymentInternalService;

    @GetMapping("/{paymentId}")
    public PaymentInternalResponse getPayment(@PathVariable UUID paymentId) {
        return paymentInternalService.getPayment(paymentId);
    }

    @PatchMapping("failed")
    public int updatePaymentFailed(@RequestBody PaymentStatusUpdateRequest request) {
        return paymentInternalService.updatePaymentFailed(request);
    }

    /** payment pending 상태로 준비 */
    @PostMapping
    public UUID preparePayment(@RequestBody PaymentInternalRequest request) {
        return paymentInternalService.preparePayment(request);
    }
}
