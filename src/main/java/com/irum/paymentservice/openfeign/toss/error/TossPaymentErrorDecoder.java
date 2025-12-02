package com.irum.paymentservice.openfeign.toss.error;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.paymentservice.global.exception.business.PaymentAlreadyProcessedException;
import com.irum.paymentservice.global.exception.business.PaymentRejectedException;
import com.irum.paymentservice.global.exception.business.PaymentTossServerException;
import com.irum.paymentservice.openfeign.toss.dto.TossPaymentsErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
public class TossPaymentErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;
    private final ErrorDecoder defaultErrorDecoder = new Default();


    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.resolve(response.status());

        // 응답 파싱
        TossPaymentsErrorResponse errorResponse = parseErrorResponse(response);
        String errorCode = (errorResponse != null && errorResponse.errorDetail() != null) ?
                errorResponse.errorDetail().code() : "UNKNOWN";
        String errorMessage = (errorResponse != null && errorResponse.errorDetail() != null) ?
                errorResponse.errorDetail().message() : "No message";

        if (status.is5xxServerError()) {
            log.warn("[Toss] Server Error: [{}]: code = {}, message = {}", response.status(), errorCode, errorMessage);
            return new PaymentTossServerException(response.status(), errorMessage, response.request());
        } else if (status.is4xxClientError()) {
            if ( status == HttpStatus.CONFLICT || status == HttpStatus.UNPROCESSABLE_ENTITY || "ALREADY_PROCESSED_PAYMENT".equals(errorCode)) {
                log.info("[Toss] 중복된 요청입니다. : [{}] code = {}, message = {}", response.status(), errorCode, errorMessage);
                return new PaymentAlreadyProcessedException(errorMessage);
            }
            log.warn("[Toss] 결제 거절({}): code = {}, message = {}", status, errorCode, errorMessage);
            return new PaymentRejectedException(errorMessage);
        }


        return defaultErrorDecoder.decode(methodKey, response);
    }

    private TossPaymentsErrorResponse parseErrorResponse(Response response) {
        if (response.body() == null) {
            return null;
        }

        try (InputStream bodyIs = response.body().asInputStream()) {
            return objectMapper.readValue(bodyIs, TossPaymentsErrorResponse.class);
        } catch (IOException e) {
            log.error("[Toss] Error Response Parsing Failed", e);
            return null;
        }

    }
}
