package com.irum.paymentservice.openfeign.toss;

import com.irum.paymentservice.domain.payment.dto.request.PaymentRequest;
import com.irum.paymentservice.global.infrastructure.properties.TossProperties;
import com.irum.paymentservice.openfeign.toss.client.TosspaymentsClient;
import com.irum.paymentservice.openfeign.toss.dto.TossPaymentsRequest;
import com.irum.paymentservice.openfeign.toss.dto.TossPaymentsResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class TosspaymentsAPI {
    private final TossProperties tossProperties;
    private final TosspaymentsClient tosspaymentsClient;

    public TossPaymentsResponse confirmPayment(
            PaymentRequest request, int paymentAmount, String idempotencyKey) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes =
                encoder.encode((tossProperties.secretKey() + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        // request 제작
        TossPaymentsRequest tossPaymentsRequest =
                new TossPaymentsRequest(
                        request.tossPaymentKey(), request.tossOrderId(), paymentAmount);
        log.info("Toss Payment Request: {}", tossPaymentsRequest.toString());

        return tosspaymentsClient.confirmPayment(
                idempotencyKey, authorizations, tossPaymentsRequest);
    }
}
