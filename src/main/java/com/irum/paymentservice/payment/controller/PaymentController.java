package com.irum.paymentservice.payment.controller;

import com.irum.paymentservice.payment.dto.request.PaymentRequest;
import com.irum.paymentservice.payment.dto.response.PaymentResponse;
import com.irum.paymentservice.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse paymentCreate(@RequestBody PaymentRequest request) {
        return paymentService.createPayment(request);
    }
}
