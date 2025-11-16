package com.irum.paymentservice.global.config;

import com.irum.global.infrastructure.config.GlobalAutoConfiguration;
import com.irum.paymentservice.domain.payment.internal.service.PaymentInternalService;
import com.irum.paymentservice.domain.payment.service.PaymentService;
import com.irum.paymentservice.domain.payment.service.PaymentServiceTest;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(GlobalAutoConfiguration.class)
public class TestConfig {
    @Bean
    public PaymentService paymentService() {
       return Mockito.mock(PaymentService.class);
   }

    @Bean
    public PaymentInternalService paymentInternalService() {
       return Mockito.mock(PaymentInternalService.class);
   }
}
