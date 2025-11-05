package com.irum.paymentservice.domain.payment.internal.service;

import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentRepository;
import com.irum.paymentservice.domain.payment.internal.dto.request.PaymentInternalRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentInternalService {
    private final PaymentRepository paymentRepository;

    public int updatePaymentFailed(PaymentInternalRequest request) {
        return paymentRepository.updateStatusToFailedByIds(request.paymetIdList());
    }
}
