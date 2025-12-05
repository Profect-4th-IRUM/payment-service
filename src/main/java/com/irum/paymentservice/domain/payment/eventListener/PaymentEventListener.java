package com.irum.paymentservice.domain.payment.eventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.global.advice.exception.CommonException;
import com.irum.paymentservice.domain.payment.domain.entity.PaymentOutbox;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentOutboxRepository;
import com.irum.paymentservice.domain.payment.event.PaymentFailedOutboxEvent;
import com.irum.paymentservice.domain.payment.event.PaymentPaidOutboxEvent;
import com.irum.paymentservice.global.exception.errorcode.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {
    private final ObjectMapper objectMapper;
    private final PaymentOutboxRepository paymentOutboxRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePaymentPaidOutboxEvent(PaymentPaidOutboxEvent event) {
        saveOutbox(event.payload(), event.paymentId().toString(), "paid");
        log.info("[DB] 결제 성공 Outbox 저장 완료. paymentId={}", event.paymentId());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePaymentFailedOutboxEvent(PaymentFailedOutboxEvent event) {
        saveOutbox(event.payload(), event.paymentId().toString(), "failed");
        log.info("[DB] 결제 실패 Outbox 저장 완료. paymentId={}", event.paymentId());
    }

    private void saveOutbox(Object payloadObj, String paymentId, String status){
        try {
            String jsonPayload = objectMapper.writeValueAsString(payloadObj);
            PaymentOutbox outbox = PaymentOutbox.builder()
                    .aggregateId(paymentId)
                    .aggregateType("p_payment")
                    .type(status)
                    .payload(jsonPayload)
                    .build();
            paymentOutboxRepository.save(outbox);
        } catch (JsonProcessingException e){
            log.error("[비즈니스] outbox 저장 중 메시지 Json 변환 에러");
            throw new CommonException(GlobalErrorCode.JSON_PROCESSING_EXCEPTION);
        }
    }
}
