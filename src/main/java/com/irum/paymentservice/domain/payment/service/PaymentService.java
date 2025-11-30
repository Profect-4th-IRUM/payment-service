package com.irum.paymentservice.domain.payment.service;

import com.irum.global.advice.exception.CommonException;
import com.irum.openfeign.order.enums.OrderStatus;
import com.irum.paymentservice.domain.payment.domain.entity.Payment;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.paymentservice.domain.payment.domain.repository.PaymentRepository;
import com.irum.paymentservice.domain.payment.dto.request.PaymentRequest;
import com.irum.paymentservice.domain.payment.dto.response.PaymentResponse;
import com.irum.paymentservice.domain.payment.producer.PaymentEventProducer;
import com.irum.paymentservice.global.exception.errorcode.PaymentErrorCode;
import com.irum.paymentservice.global.util.MemberUtil;
import com.irum.paymentservice.openfeign.order.OrderAPI;
import com.irum.paymentservice.openfeign.toss.TosspaymentsAPI;
import com.irum.paymentservice.openfeign.toss.dto.TossPaymentsResponse;
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
    private final TosspaymentsAPI tosspaymentsAPI;
    private final PaymentRepository paymentRepository;
    private final MemberUtil memberUtil;
    private final OrderAPI orderAPI;
    private final PaymentStatusService paymentStatusService;
    private final PaymentEventProducer paymentEventProducer;

    public PaymentResponse createPayment(PaymentRequest request) {
        log.info("[요청] payment request: {}", request.toString());
        Payment payment =
                paymentRepository
                        .findById(request.paymentId())
                        .orElseThrow(() -> new CommonException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        log.info("[조회] Payment 조회 완료 {}", payment.getPaymentId());

        // 접근성 확인
        memberUtil.assertMemberResourceAccess(payment.getMemberId());
        log.info("[검증] 멤버 검증 완료 {}", payment.getPaymentId());

        // pending 상태인지 확인 (이미 처리된 결제 등)
        if (!payment.getPaymentStatus().equals(PaymentStatus.PENDING)) {
            log.info("[검증] 이미 처리된 결제입니다 {}", payment.getPaymentStatus());
            throw new CommonException(PaymentErrorCode.PAYMENT_BAD_REQUEST);
        }
        log.info("Creating payment with payment id {}", payment.getPaymentId());

        try {
            // 토스 페이먼츠 승인 API호출
            TossPaymentsResponse tossPaymentsResponse =
                    tosspaymentsAPI.confirmPayment(request, payment.getAmount());
            log.info("[외부] 토스 페이먼츠 승인 완료");

            // 상태 업데이트
            payment.updateToPaid(tossPaymentsResponse);
            log.info("[DB] Payment Paid 상태 업데이트 완료");

            paymentEventProducer.sendPaymentPaidEvent(OrderStatus.PREPARING, request.orderId());
            log.info("[외부] paymentPaidEvent 발행 완료");

            return new PaymentResponse(payment.getAmount());
        } catch (FeignException e) {
            // 추후에 Feign client error decoder로 변환하면 좋을 듯

            // 상태 업데이트
            paymentStatusService.updatePaymentStatusFailed(payment.getPaymentId());

            // order, orderdetail 상태 업데이트, 재고 롤백, 쿠폰 롤백
            paymentEventProducer.sendPaymentFailedEvent(request.orderId(), request.paymentId(), OrderStatus.FAILED);
            log.info("[외부] paymentFailedEvent 발행 완료");

//            try {
//                // order, orderdetail 상태 업데이트, 재고 롤백, 쿠폰 롤백
//                orderAPI.updateOrderStatusFailed(
//                        OrderStatus.FAILED, request.orderId(), request.paymentId());
//            } catch (Exception orderException) {
//                log.error("[에러] exception {} {}", orderException, orderException.getMessage());
//            }
//            log.info("[Feign] orderAPI.updateOrderStatusFailed 완료");

            throw new CommonException(PaymentErrorCode.PAYMENT_ERROR);
        }
    }
}
