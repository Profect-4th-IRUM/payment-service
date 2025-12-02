package com.irum.paymentservice.openfeign.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.paymentservice.openfeign.toss.error.TossPaymentErrorDecoder;
import feign.Logger;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {
    private final ObjectMapper objectMapper;


    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    // 응답이 200이 아닐 경우 실행되는 로직
    @Bean
    public ErrorDecoder errorDecoder() {
        return new TossPaymentErrorDecoder(objectMapper);
    }

    @Bean
    public Retryer retryer() {
        // 100ms 시작, 최대 1초 대기, 총 3회 시도, 1.5 지수 백오프
        return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(1), 3);
    }
}
