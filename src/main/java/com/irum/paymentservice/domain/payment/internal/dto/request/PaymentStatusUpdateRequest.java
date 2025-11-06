package com.irum.paymentservice.domain.payment.internal.dto.request;

import java.util.List;
import java.util.UUID;

public record PaymentStatusUpdateRequest(
        List<UUID> paymetIdList
) {
}
