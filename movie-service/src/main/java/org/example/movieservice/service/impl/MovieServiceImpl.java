package org.example.movieservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.movieservice.dto.request.CreateMovieRequest;
import org.example.movieservice.dto.response.MovieResponse;
import org.example.movieservice.exception.CustomException;
import org.example.movieservice.exception.ErrorMessages;
import org.example.movieservice.model.Genre;
import org.example.movieservice.model.Movie;
import org.example.movieservice.repository.MovieRepository;
import org.example.movieservice.service.GenreService;
import org.example.movieservice.service.MovieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final GenreService genreService;

    @Override
    @Transactional
    public MovieResponse createMovie(CreateMovieRequest createMovieRequest) {
        if (movieRepository.existsByTitle(createMovieRequest.title())) {
            throw new CustomException(ErrorMessages.MOVIE_ALREADY_EXISTS);
        }
        Set<Genre> genres = genreService.findAllById(createMovieRequest.genres());

        Movie movie = Movie.builder()
                .title(createMovieRequest.title())
                .genres(genres)
                .durationMinutes(createMovieRequest.durationMinutes())
                .build();
        Movie save = movieRepository.save(movie);

        return MovieResponse.builder()
                .id(save.getId())
                .title(save.getTitle())
                .genres(save.getGenres())
                .durationMinutes(save.getDurationMinutes())
                .build();
    }

    @Override
    public MovieResponse getMovie(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorMessages.MOVIE_DOESNT_EXISTS));
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genres(movie.getGenres())
                .durationMinutes(movie.getDurationMinutes())
                .build();
    }

    @Override
    public Movie findById(UUID id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorMessages.MOVIE_DOESNT_EXISTS));
    }

    @Override
    public List<MovieResponse> getMovies() {
        return movieRepository.findAll()
                .stream()
                .map(mv -> MovieResponse.builder()
                        .id(mv.getId())
                        .title(mv.getTitle())
                        .genres(mv.getGenres())
                        .durationMinutes(mv.getDurationMinutes())
                        .build())
                .toList();
    }

}
