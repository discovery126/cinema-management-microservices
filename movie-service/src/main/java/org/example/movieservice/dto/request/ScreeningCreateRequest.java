package org.example.movieservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public record ScreeningCreateRequest(String hall,
                                     @JsonProperty(value = "total_seats")
                                     Integer totalSeats,
                                     BigDecimal price,
                                     @JsonProperty(value = "starts_at")
                                     Instant startsAt) {}