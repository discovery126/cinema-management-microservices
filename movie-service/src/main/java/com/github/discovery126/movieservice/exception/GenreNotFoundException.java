package com.github.discovery126.movieservice.exception;

import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
public class GenreNotFoundException extends RuntimeException {
    private final Set<UUID> notFoundIds;

    public GenreNotFoundException(Set<UUID> notFoundIds) {
        super("Genres not found: " + notFoundIds);
        this.notFoundIds = notFoundIds;
    }
}