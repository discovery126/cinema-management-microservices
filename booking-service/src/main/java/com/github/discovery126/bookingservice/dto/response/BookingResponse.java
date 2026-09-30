package com.github.discovery126.bookingservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import com.github.discovery126.bookingservice.model.BookingStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record BookingResponse(UUID id,
                              @JsonProperty(value = "screening_id")
                              UUID screeningId,
                              @JsonProperty(value = "customer_name")
                              String customerName,
                              @JsonProperty(value = "seats_count")
                              Integer seatsCount,
                              @JsonProperty(value = "total_price")
                              BigDecimal totalPrice,
                              BookingStatus status,
                              @JsonProperty(value = "created_at")
                              Instant createdAt) { }
