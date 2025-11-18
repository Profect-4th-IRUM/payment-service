package com.irum.paymentservice.domain.payment.internal;

import com.irum.openfeign.payment.dto.response.PaymentResponse;
import com.irum.openfeign.payment.emuns.PaymentMethod;
import com.irum.openfeign.payment.emuns.PaymentStatus;
import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.internal.dto.response.PaymentInternalResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentResponseMapper {
    public static PaymentResponse toPaymentResponse(Payment p) {
        return new PaymentResponse(PaymentStatus.valueOf(p.getPaymentStatus().toString()), PaymentMethod.valueOf(p.getPaymentMethod().toString()));

    }
}
