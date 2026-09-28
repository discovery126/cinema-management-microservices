package org.example.movieservice.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record GenreResponse(UUID id, String name) {}
