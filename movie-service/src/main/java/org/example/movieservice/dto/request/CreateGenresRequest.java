package org.example.movieservice.dto.request;

import java.util.List;

public record CreateGenresRequest(
        List<String> genres
) {}