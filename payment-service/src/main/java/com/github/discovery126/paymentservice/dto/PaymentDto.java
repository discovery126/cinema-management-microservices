package com.github.discovery126.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentDto(@JsonProperty(value = "booking_id")
                         UUID bookingId,
                         BigDecimal amount) {
}
