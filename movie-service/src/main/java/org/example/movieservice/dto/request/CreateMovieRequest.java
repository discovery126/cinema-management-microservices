package org.example.movieservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;
import java.util.UUID;

public record CreateMovieRequest(String title,
                                 Set<UUID> genres,
                                 @JsonProperty(value = "duration_minutes")
                                 Integer durationMinutes) {
}
