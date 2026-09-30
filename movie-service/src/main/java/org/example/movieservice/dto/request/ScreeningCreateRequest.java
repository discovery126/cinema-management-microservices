package org.example.movieservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public record ScreeningCreateRequest(
        @NotBlank(message = "Hall must not be blank")
        @Size(max = 10, message = "Hall must be at most 10 characters")
        String hall,

        @NotNull(message = "Total seats must not be null")
        @Positive(message = "Total seats must be positive")
        @JsonProperty("total_seats")
        Integer totalSeats,

        @NotNull(message = "Price must not be null")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotNull(message = "Start time must not be null")
        @JsonProperty("starts_at")
        Instant startsAt
) {}