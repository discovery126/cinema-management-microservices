package org.example.movieservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.movieservice.dto.request.CreateMovieRequest;
import org.example.movieservice.dto.response.MovieResponse;
import org.example.movieservice.exception.CustomException;
import org.example.movieservice.exception.ErrorMessages;
import org.example.movieservice.mapper.MovieMapper;
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
    private final MovieMapper movieMapper;

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

        return movieMapper.toMovieResponse(save);
    }

    @Override
    public MovieResponse getMovie(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorMessages.MOVIE_DOESNT_EXISTS));
        return movieMapper.toMovieResponse(movie);
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
                .map(movieMapper::toMovieResponse)
                .toList();
    }

}
