package com.irum.paymentservice.global.infrastructure.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        TossProperties.class,
        KafkaProperties.class,
        KafkaTopicProperties.class
})
public class PropertiesConfig {}
