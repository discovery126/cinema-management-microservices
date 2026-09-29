package org.example.movieservice.service.impl;

import org.example.movieservice.dto.request.ScreeningCreateRequest;
import org.example.movieservice.dto.response.ScreeningResponse;
import org.example.movieservice.exception.CustomException;
import org.example.movieservice.exception.ErrorMessages;
import org.example.movieservice.mapper.ScreeningMapper;
import org.example.movieservice.model.Genre;
import org.example.movieservice.model.Movie;
import org.example.movieservice.model.Screening;
import org.example.movieservice.repository.ScreeningRepository;
import org.example.movieservice.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceImplTest {

    @Mock
    private MovieService movieService;

    @Mock
    private ScreeningRepository screeningRepository;

    @InjectMocks
    private ScreeningServiceImpl screeningService;

    private ScreeningCreateRequest screeningCreateRequest;

    private UUID testMovieId;
    private Movie testMovie;
    private Screening testScreening;
    private ScreeningResponse testScreeningResponse;

    @BeforeEach
    void setup() {
        ScreeningMapper screeningMapper = new ScreeningMapper();
        screeningService = new ScreeningServiceImpl(
                movieService,
                screeningRepository,
                screeningMapper
        );
        ReflectionTestUtils.setField(screeningService, "timeCleanScreening", 15);
        screeningCreateRequest = new ScreeningCreateRequest(
                "A",
                100,
                new BigDecimal("1000.0"),
                Instant.parse("2024-01-01T10:00:00Z")
        );
        testMovieId = UUID.randomUUID();

        Set<Genre> testGenresSet = Set.of(
                new Genre(UUID.randomUUID(), "Драма"),
                new Genre(UUID.randomUUID(), "Фантастика")
        );
        testMovie = Movie.builder()
                .id(UUID.randomUUID())
                .title("title")
                .durationMinutes(100)
                .genres(testGenresSet)
                .build();
        testScreening = Screening.builder()
                .id(UUID.randomUUID())
                .movie(testMovie)
                .hall("A")
                .startsAt(Instant.parse("2026-01-01T10:00:00Z"))
                .endsAt(Instant.parse("2026-01-01T11:40:00Z"))
                .totalSeats(50)
                .availableSeats(50)
                .price(new BigDecimal("1000.0"))
                .build();

        testScreeningResponse = screeningMapper.toScreeningResponse(testScreening);
    }

    @Test
    void shouldReturnCustomExceptionWhenCreateScreeningWithOverlapOthers() {
        //when
        when(movieService.findById(testMovieId)).thenReturn(testMovie);
        when(screeningRepository.existsOverlapping(
                eq(screeningCreateRequest.hall()),
                any(Instant.class),
                any(Instant.class))
        ).thenReturn(Boolean.TRUE);
        //then
        assertThatThrownBy(() -> screeningService.createScreening(screeningCreateRequest, testMovieId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorMessages.SCREENING_OVERLAP);
        verify(movieService).findById(testMovieId);
    }

    @Test
    void shouldReturnScreeningResponseWhenCreateScreeningWithoutOverlap() {
        // given

        when(movieService.findById(testMovieId)).thenReturn(testMovie);
        when(screeningRepository.existsOverlapping(any(), any(), any())).thenReturn(false);
        when(screeningRepository.save(any(Screening.class))).thenReturn(testScreening);
        //when
        ScreeningResponse result = screeningService.createScreening(screeningCreateRequest, testMovieId);

        // then
        assertNotNull(result);
        assertEquals(testScreening.getId(), result.id());
        verify(movieService).findById(testMovieId);
        verify(screeningRepository).existsOverlapping(any(), any(), any());
        verify(screeningRepository).save(any(Screening.class));
    }
    @Test
    void shouldReturnScreeningResponseWhenFoundById() {
        //given
        UUID id = testScreening.getId();
        when(screeningRepository.findById(id)).thenReturn(Optional.of(testScreening));
        //when
        ScreeningResponse result = screeningService.getScreening(id);
        //then
        assertNotNull(result);
        assertEquals(testScreeningResponse, result);
        verify(screeningRepository).findById(id);
    }
    @Test
    void shouldThrowExceptionWhenScreeningNotFound() {
        //given
        UUID id = UUID.randomUUID();
        when(screeningRepository.findById(id)).thenReturn(Optional.empty());
        //when & then
        assertThatThrownBy(() -> screeningService.getScreening(id))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorMessages.SCREENING_DOESNT_EXISTS);

        verify(screeningRepository).findById(id);
    }
    @Test
    void shouldReturnAllScreeningsWhenRepositoryNotEmpty() {
        //given
        List<Screening> screenings = List.of(testScreening);
        when(screeningRepository.findAll()).thenReturn(screenings);
        //when
        List<ScreeningResponse> result = screeningService.getAllScreening();
        //then
        assertEquals(List.of(testScreeningResponse), result);
        verify(screeningRepository).findAll();
    }
    @Test
    void shouldReturnEmptyListWhenRepositoryIsEmpty() {
        //given
        when(screeningRepository.findAll()).thenReturn(List.of());
        //when
        List<ScreeningResponse> result = screeningService.getAllScreening();
        //then
        assertTrue(result.isEmpty());
        verify(screeningRepository).findAll();
    }
    @Test
    void shouldReturnScreeningsByMovieIdWhenRepositoryNotEmpty() {
        //given
        UUID movieId = testMovie.getId();
        List<Screening> screenings = List.of(testScreening);
        when(screeningRepository.findAllByMovieId(movieId)).thenReturn(screenings);
        //when
        List<ScreeningResponse> result = screeningService.getAllScreeningByMovieId(movieId);
        //then
        assertEquals(List.of(testScreeningResponse), result);
        verify(screeningRepository).findAllByMovieId(movieId);
    }
    @Test
    void shouldReturnEmptyListWhenNoScreeningsForMovie() {
        //given
        UUID movieId = UUID.randomUUID();
        when(screeningRepository.findAllByMovieId(movieId)).thenReturn(List.of());
        //when
        List<ScreeningResponse> result = screeningService.getAllScreeningByMovieId(movieId);
        //then
        assertTrue(result.isEmpty());
        verify(screeningRepository).findAllByMovieId(movieId);
    }

}