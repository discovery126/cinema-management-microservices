package org.example.bookingservice.dto.movie;

import java.util.UUID;

public record GenreDto(
        UUID id,
        String name
) {}
