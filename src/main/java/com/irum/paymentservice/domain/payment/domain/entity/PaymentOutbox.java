package com.irum.paymentservice.domain.payment.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_payment_outbox")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOutbox {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "payment_outbox_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "type", nullable = false)
    private String type; // failed, paid

    @Column(name = "aggregatetype", nullable = false) // Debezium 표준 컬럼명: aggregatetype
    private String aggregateType; // 어느 table 발생했는지

    @Column(name = "aggregateid", nullable = false) // Debezium 표준 컬럼명: aggregateid
    private String aggregateId; // table id

    @Column(columnDefinition = "jsonb", nullable = false)
    private String payload;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
