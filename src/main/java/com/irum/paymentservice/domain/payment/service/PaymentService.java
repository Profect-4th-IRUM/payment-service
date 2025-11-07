package com.irum.paymentservice.domain.payment.service;

import com.irum.global.advice.exception.CommonException;
import com.irum.paymentservice.openfeign.order.OrderClient;
import com.irum.paymentservice.openfeign.order.dto.enums.OrderStatus;
import com.irum.paymentservice.openfeign.toss.TosspaymentsClient;
import com.irum.paymentservice.openfeign.toss.dto.TossPaymentsResponse;
import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentRepository;
import com.irum.paymentservice.domain.payment.dto.request.PaymentRequest;
import com.irum.paymentservice.domain.payment.dto.response.PaymentResponse;
import com.irum.paymentservice.global.exception.errorcode.PaymentErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    private final TosspaymentsClient tosspaymentsClient;
    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;

    public PaymentResponse createPayment(PaymentRequest request) {
        Payment payment =
                paymentRepository
                        .findById(request.paymentId())
                        .orElseThrow(() -> new CommonException(PaymentErrorCode.PAYMENT_ERROR));

        // pending 상태인지 확인 (이미 처리된 결제 등)
        if (!payment.getPaymentStatus().equals(PaymentStatus.PENDING)) {
            throw new CommonException(PaymentErrorCode.PAYMENT_BAD_REQUEST);
        }

        try {
            // 토스 페이먼츠 승인 API호출
            TossPaymentsResponse tossPaymentsResponse =
                    tosspaymentsClient.confirmPayment(request, payment.getAmount());

            // 상태 업데이트
            payment.updateToPaid(PaymentStatus.PAID, tossPaymentsResponse);

            // order, orderdetail 상태 업데이트
            String OrderNum =
                    orderClient.updateOrderStatusPreparing(
                            OrderStatus.PREPARING, request.orderId());

            return new PaymentResponse(OrderNum, payment.getAmount());
        } catch (FeignException e) {
            // 추후에 Feign client error decoder로 변환하면 좋을 듯

            // 상태 업데이트
            payment.updateStatus(PaymentStatus.FAILED);

            // order, orderdetail 상태 업데이트, 재고 롤백, 쿠폰 롤백
            orderClient.updateOrderStatusFailed(
                    OrderStatus.FAILED, request.orderId(), request.paymentId());

            throw new CommonException(PaymentErrorCode.PAYMENT_ERROR);
        }
    }
}
