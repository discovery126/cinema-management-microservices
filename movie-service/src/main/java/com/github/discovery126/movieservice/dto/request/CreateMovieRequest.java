package com.github.discovery126.movieservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record CreateMovieRequest(

        @NotBlank(message = "Title must not be blank")
        @Size(max = 255, message = "Title must be at most 255 characters")
        String title,

        @NotEmpty(message = "Genres must not be empty")
        Set<@NotNull UUID> genres,

        @NotNull(message = "Duration must not be null")
        @Positive(message = "Duration must be positive")
        @JsonProperty("duration_minutes")
        Integer durationMinutes
) {}