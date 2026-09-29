package org.example.bookingservice.dto.movie;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record MovieDto(
        UUID id,
        String title,
        List<GenreDto> genres,
        @JsonProperty("duration_minutes") Integer durationMinutes
) {}
