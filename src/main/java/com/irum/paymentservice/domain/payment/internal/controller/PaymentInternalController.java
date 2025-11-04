package com.irum.paymentservice.domain.payment.internal.controller;

import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentInternalRequest;
import com.irum.paymentservice.domain.payment.internal.service.PaymentInternalService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/internal/payment")
@RequiredArgsConstructor
public class PaymentInternalController {

    private final PaymentInternalService paymentInternalService;

    /** payment pending 상태로 준비 */
    @PostMapping
    public UUID preparePayment(@RequestBody PaymentInternalRequest request) {
        return paymentInternalService.preparePayment(request);
    }
}
