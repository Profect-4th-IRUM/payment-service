package com.irum.paymentservice.domain.payment.event;

import java.util.UUID;

public record PaymentFailedOutboxEvent (
        UUID paymentId,
        PaymentFailedEvent payload
){
}
