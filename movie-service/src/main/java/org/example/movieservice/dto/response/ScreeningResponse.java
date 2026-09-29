package org.example.movieservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Builder
public record ScreeningResponse(UUID id,
                                @JsonProperty(value = "movie")
                                MovieResponse movieResponse,
                                String hall,
                                @JsonProperty(value = "total_seats")
                                Integer totalSeats,
                                @JsonProperty(value = "available_seats")
                                Integer availableSeats,
                                BigDecimal price,
                                @JsonProperty(value = "starts_at")
                                Instant startsAt,
                                @JsonProperty(value = "ends_at")
                                Instant endsAt) {}
