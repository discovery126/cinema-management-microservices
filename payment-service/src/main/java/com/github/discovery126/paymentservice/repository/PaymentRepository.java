package com.github.discovery126.paymentservice.repository;

import com.github.discovery126.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
