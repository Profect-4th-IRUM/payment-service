package com.irum.paymentservice.domain.payment.domain.entity;

import com.irum.global.domain.BaseEntity;
import com.irum.paymentservice.openfeign.toss.dto.TossPaymentsResponse;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentCorp;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentMethod;
import com.irum.paymentservice.domain.payment.domain.entity.enums.PaymentStatus;
import jakarta.persistence.*;

import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
@NoArgsConstructor
@Entity
@Getter
@Table(name = "p_payment")
public class Payment extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "payment_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private int amount;

    private int totalDiscountAmount;

    private String tossPaymentKey;

    private String tossOrderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentCorp paymentCorp;

    private Long memberId;

    public void updateStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void updateToPaid(PaymentStatus ps, TossPaymentsResponse res) {
        this.paymentStatus = ps;
        this.tossPaymentKey = res.paymentKey();
        this.tossOrderId = res.orderId();
        this.paymentMethod = PaymentMethod.from(res.method());
    }
}
