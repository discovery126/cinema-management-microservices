package com.github.discovery126.bookingservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record BookingCreateRequest(@JsonProperty(value = "screening_id")
                                   UUID screeningId,
                                   @JsonProperty(value = "customer_name")
                                   String customerName,
                                   @JsonProperty(value = "seats_count")
                                   Integer seatsCount) {
}
