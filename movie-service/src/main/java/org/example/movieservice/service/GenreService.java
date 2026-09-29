package org.example.movieservice.service;

import org.example.movieservice.dto.request.CreateGenresRequest;
import org.example.movieservice.dto.response.GenreResponse;
import org.example.movieservice.model.Genre;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface GenreService {
    void createGenres(CreateGenresRequest createGenresRequestList);
    Set<Genre> findAllById(Set<UUID> ids);
    List<GenreResponse> getGenres();
}
