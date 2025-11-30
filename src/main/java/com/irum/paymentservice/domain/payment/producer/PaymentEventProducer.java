package com.irum.paymentservice.domain.payment.producer;

import com.irum.openfeign.order.enums.OrderStatus;
import com.irum.paymentservice.domain.payment.event.PaymentFailedEvent;
import com.irum.paymentservice.domain.payment.event.PaymentPaidEvent;
import com.irum.paymentservice.global.infrastructure.properties.KafkaTopicProperties;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, PaymentPaidEvent> paymentPaidEventKafkaTemplate;
    private final KafkaTemplate<String, PaymentFailedEvent> paymentFailedEventKafkaTemplate;
    private final KafkaTopicProperties kafkaTopicProperties;

    public void sendPaymentPaidEvent(OrderStatus orderStatus, UUID orderId) {
        PaymentPaidEvent event = PaymentPaidEvent.from(orderStatus, orderId);
        String key = event.orderId().toString();
        ProducerRecord<String, PaymentPaidEvent> record =
                new ProducerRecord<>(kafkaTopicProperties.paymentPaid(), key, event);

        try {
            paymentPaidEventKafkaTemplate
                    .send(record)
                    .whenComplete(
                            (result, exception) -> {
                                if (exception == null) {
                                    // 성공
                                    log.debug("[Success] Sent payment paid event: {}", event);
                                } else {
                                    // 실패 처리
                                    log.error(
                                            "[Error] sending payment paid event : {}",
                                            exception.getMessage(),
                                            exception);
                                }
                            });
        } catch (Exception e) {
            log.error("[Error] sending payment paid event : {}", event, e);
        }
    }

    public void sendPaymentFailedEvent(UUID orderId, UUID paymentId, OrderStatus orderStatus) {
        PaymentFailedEvent event = PaymentFailedEvent.from(orderId, paymentId, orderStatus);
        String key = event.orderId().toString();
        ProducerRecord<String, PaymentFailedEvent>  record =
                new ProducerRecord<>(kafkaTopicProperties.paymentFailed(), key, event);

        try {
            paymentFailedEventKafkaTemplate
                    .send(record)
                    .whenComplete(
                            (result, exception) -> {
                                if (exception == null) {
                                    // 성공
                                    log.debug("[Success] Sent payment failed event: {}", event);
                                } else {
                                    // 실패 처리
                                    log.error(
                                            "[Error] sending payment failed event : {}",
                                            exception.getMessage(),
                                            exception);
                                }
                            });
        } catch (Exception e) {
            log.error("[Error] sending payment failed event : {}", event, e);
        }

    }
}
