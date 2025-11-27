package com.irum.paymentservice.global.infrastructure.config;

import com.irum.paymentservice.global.infrastructure.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.internals.Topic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    public NewTopic paymentPaidTopic() {
        return TopicBuilder.name("payments-paid")
                .partitions(kafkaProperties.partition())
                .replicas(kafkaProperties.replica())
                .config(
                        TopicConfig.RETENTION_MS_CONFIG,
                        String.valueOf(7 * 24 * 60 * 60 * 1000L) // 7일간 메시지 보관
                ).build();
    }

    @Bean
    public NewTopic paymentFailTopic() {
        return TopicBuilder.name("payments-failed")
                .partitions(kafkaProperties.partition())
                .replicas(kafkaProperties.replica())
                .config(
                        TopicConfig.RETENTION_MS_CONFIG,
                        String.valueOf(7 * 24 * 60 * 60 * 1000L) // 7일간 메시지 보관
                ).build();
    }
}
