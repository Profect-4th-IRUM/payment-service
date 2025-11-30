package com.irum.paymentservice.global.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.topics")
public record KafkaTopicProperties(String paymentPaid, String paymentFailed) {}
