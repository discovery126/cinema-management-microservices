package org.example.movieservice.service;

import org.example.movieservice.dto.request.CreateGenresRequest;
import org.example.movieservice.dto.response.GenreResponse;

import java.util.List;

public interface GenreService {
    void createGenres(CreateGenresRequest createGenresRequestList);
    List<GenreResponse> getGenres();
}
