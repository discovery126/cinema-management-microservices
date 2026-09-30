package org.example.movieservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;                          // ← добавили
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

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final GenreService genreService;
    private final MovieMapper movieMapper;

    @Override
    @Transactional
    public MovieResponse createMovie(CreateMovieRequest createMovieRequest) {
        log.info("Creating movie: title='{}', duration={}min, genres={}",
                createMovieRequest.title(),
                createMovieRequest.durationMinutes(),
                createMovieRequest.genres());

        if (movieRepository.existsByTitle(createMovieRequest.title())) {
            log.warn("Movie with title='{}' already exists", createMovieRequest.title());
            throw new CustomException(ErrorMessages.MOVIE_ALREADY_EXISTS);
        }

        Set<Genre> genres = genreService.findAllById(createMovieRequest.genres());
        log.debug("Resolved {} genres for movie '{}'", genres.size(), createMovieRequest.title());

        Movie movie = Movie.builder()
                .title(createMovieRequest.title())
                .genres(genres)
                .durationMinutes(createMovieRequest.durationMinutes())
                .build();
        Movie save = movieRepository.save(movie);

        log.info("Movie created: id={}, title='{}'", save.getId(), save.getTitle());
        return movieMapper.toMovieResponse(save);
    }

    @Override
    public MovieResponse getMovie(UUID id) {
        log.debug("Fetching movie id={}", id);
        Movie movie = findById(id);
        log.debug("Movie id={} found: title='{}'", id, movie.getTitle());
        return movieMapper.toMovieResponse(movie);
    }

    @Override
    public Movie findById(UUID id) {
        log.debug("Finding movie entity id={}", id);
        return movieRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Movie id={} not found", id);
                    return new CustomException(ErrorMessages.MOVIE_DOESNT_EXISTS);
                });
    }

    @Override
    public List<MovieResponse> getMovies() {
        log.debug("Fetching all movies");
        List<MovieResponse> movies = movieRepository.findAll()
                .stream()
                .map(movieMapper::toMovieResponse)
                .toList();
        log.debug("Found {} movies", movies.size());
        return movies;
    }
}