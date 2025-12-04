package com.irum.paymentservice.domain.payment.domain.repository;

import com.irum.paymentservice.domain.payment.domain.entity.PaymentOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentOutboxRepository extends JpaRepository<PaymentOutbox, UUID> {
}
