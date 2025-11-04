package com.irum.paymentservice.domain.payment.internal.controller;

import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.dto.request.PaymentRequest;
import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentInternalRequest;
import com.irum.paymentservice.domain.payment.internal.service.PaymentInternalService;
import com.irum.paymentservice.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/internal/payment")
@RequiredArgsConstructor
public class PaymentInternalController {

    private final PaymentInternalService paymentInternalService;

    /**payment pending 상태로 준비*/
    @PostMapping
    public UUID preparePayment(
            @RequestBody PaymentInternalRequest request
    ){
        return paymentInternalService.preparePayment(request);
    }
}
