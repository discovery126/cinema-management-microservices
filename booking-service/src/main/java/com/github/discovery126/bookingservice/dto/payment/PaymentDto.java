package com.github.discovery126.bookingservice.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentDto(@JsonProperty(value = "booking_id")
                         UUID bookingId,
                         BigDecimal amount) {
}
