package org.example.movieservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateGenresRequest(
        @NotEmpty
        @Size(max = 200)
        List<@NotBlank @Size(max = 100) String> genres
) {}