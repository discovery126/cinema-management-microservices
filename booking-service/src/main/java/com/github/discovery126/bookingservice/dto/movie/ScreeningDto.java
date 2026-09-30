package com.github.discovery126.bookingservice.dto.movie;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ScreeningDto (
        UUID id,
        @JsonProperty("movie")
        MovieDto movieDto,
        String hall,
        @JsonProperty("total_seats")
        Integer totalSeats,
        @JsonProperty("available_seats")
        Integer availableSeats,
        BigDecimal price,
        @JsonProperty("starts_at")
        Instant startsAt,
        @JsonProperty("ends_at")
        Instant endsAt
) {}