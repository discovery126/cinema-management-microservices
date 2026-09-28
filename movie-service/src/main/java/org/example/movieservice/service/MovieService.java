package org.example.movieservice.service;

import org.example.movieservice.dto.request.CreateMovieRequest;
import org.example.movieservice.dto.response.MovieResponse;
import org.example.movieservice.model.Movie;

import java.util.List;
import java.util.UUID;

public interface MovieService {
    MovieResponse createMovie(CreateMovieRequest createMovieRequest);
    MovieResponse getMovie(UUID id);
    List<MovieResponse> getMovies();
}
