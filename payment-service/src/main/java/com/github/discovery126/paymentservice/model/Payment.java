package com.github.discovery126.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;


}