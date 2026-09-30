package com.github.discovery126.movieservice.service;

import com.github.discovery126.movieservice.dto.request.CreateMovieRequest;
import com.github.discovery126.movieservice.dto.response.MovieResponse;
import com.github.discovery126.movieservice.model.Movie;

import java.util.List;
import java.util.UUID;

public interface MovieService {
    MovieResponse createMovie(CreateMovieRequest createMovieRequest);
    MovieResponse getMovie(UUID id);
    // for personal using
    Movie findById(UUID id);
    List<MovieResponse> getMovies();
}
