package org.example.movieservice.service.impl;

import org.example.movieservice.dto.request.CreateMovieRequest;
import org.example.movieservice.dto.response.MovieResponse;
import org.example.movieservice.exception.CustomException;
import org.example.movieservice.exception.ErrorMessages;
import org.example.movieservice.mapper.MovieMapper;
import org.example.movieservice.model.Genre;
import org.example.movieservice.model.Movie;
import org.example.movieservice.repository.MovieRepository;
import org.example.movieservice.service.GenreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GenreService genreService;

    private MovieServiceImpl movieService;

    private CreateMovieRequest testCreateMovieRequest;
    private Movie testMovie;
    private MovieResponse testMovieResponse;
    private Set<Genre> testGenresSet;
    private Set<UUID> testIdsSet;
    @BeforeEach
    void setup() {
        MovieMapper movieMapper = new MovieMapper();

        movieService = new MovieServiceImpl(movieRepository, genreService, movieMapper);

        testGenresSet = Set.of(
                new Genre(UUID.randomUUID(),"Драма"),
                new Genre(UUID.randomUUID(),"Фантастика")
        );
        testIdsSet = testGenresSet.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        testCreateMovieRequest = new CreateMovieRequest(
                "title",
                testIdsSet,
                100
        );

        testMovie = Movie.builder()
                .id(UUID.randomUUID())
                .title(testCreateMovieRequest.title())
                .durationMinutes(testCreateMovieRequest.durationMinutes())
                .genres(testGenresSet)
                .build();

        testMovieResponse = movieMapper.toMovieResponse(testMovie);
    }
    @Test
    void shouldThrowExceptionWhenTitleAlreadyExists() {
        //given
        when(movieRepository.existsByTitle(testCreateMovieRequest.title())).thenReturn(Boolean.TRUE);

        //when && then
        assertThatThrownBy(() -> movieService.createMovie(testCreateMovieRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorMessages.MOVIE_ALREADY_EXISTS);
    }

    @Test
    void shouldCreateMovieWhenTitleIsUnique() {
        //given
        when(movieRepository.existsByTitle(testCreateMovieRequest.title())).thenReturn(Boolean.FALSE);
        when(genreService.findAllById(testCreateMovieRequest.genres())).thenReturn(testGenresSet);
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        //when
        MovieResponse resultMovieResponse = movieService.createMovie(testCreateMovieRequest);

        //then
        assertEquals(resultMovieResponse, testMovieResponse);
        verify(movieRepository).existsByTitle(testCreateMovieRequest.title());
        verify(genreService).findAllById(testIdsSet);
        verify(movieRepository).save(any(Movie.class));

    }
    @Test
    void shouldThrowExceptionWhenGetMovieAndRepositoryReturnsEmpty() {
        //given
        UUID testId = UUID.randomUUID();
        when(movieRepository.findById(testId)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> movieService.getMovie(testId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorMessages.MOVIE_DOESNT_EXISTS);
    }

    @Test
    void shouldThrowExceptionWhenFindByIdAndRepositoryReturnsEmpty() {
        //given
        UUID testId = UUID.randomUUID();
        when(movieRepository.findById(testId)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> movieService.findById(testId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorMessages.MOVIE_DOESNT_EXISTS);
    }
    @Test
    void shouldReturnMovieResponseWhenFoundById() {
        //given
        UUID testId = testMovie.getId();
        when(movieRepository.findById(testId)).thenReturn(Optional.of(testMovie));

        //when
        MovieResponse resultMovieResponse = movieService.getMovie(testId);

        //then
        assertEquals(resultMovieResponse, testMovieResponse);
        verify(movieRepository).findById(testId);
    }

    @Test
    void shouldReturnAllMoviesWhenRepositoryNotEmpty() {
        // given
        List<Movie> testMovies = List.of(testMovie);
        when(movieRepository.findAll()).thenReturn(testMovies);

        // when
        List<MovieResponse> result = movieService.getMovies();

        // then
        assertEquals(List.of(testMovieResponse), result);
        verify(movieRepository).findAll();
    }
}