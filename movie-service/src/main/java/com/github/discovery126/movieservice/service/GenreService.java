package com.github.discovery126.movieservice.service;

import com.github.discovery126.movieservice.dto.request.CreateGenresRequest;
import com.github.discovery126.movieservice.dto.response.GenreResponse;
import com.github.discovery126.movieservice.model.Genre;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface GenreService {
    void createGenres(CreateGenresRequest createGenresRequestList);
    Set<Genre> findAllById(Set<UUID> ids);
    List<GenreResponse> getGenres();
}
