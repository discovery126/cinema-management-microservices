package com.github.discovery126.movieservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import com.github.discovery126.movieservice.model.Genre;

import java.util.Set;
import java.util.UUID;

@Builder
public record MovieResponse(UUID id,
                            String title,
                            Set<Genre> genres,
                            @JsonProperty(value = "duration_minutes")
                            Integer durationMinutes) {
}
