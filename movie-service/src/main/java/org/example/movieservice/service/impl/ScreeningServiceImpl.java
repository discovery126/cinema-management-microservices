package org.example.movieservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.movieservice.dto.request.ScreeningCreateRequest;
import org.example.movieservice.dto.response.ScreeningResponse;
import org.example.movieservice.exception.CustomException;
import org.example.movieservice.exception.ErrorMessages;
import org.example.movieservice.mapper.ScreeningMapper;
import org.example.movieservice.model.Movie;
import org.example.movieservice.model.Screening;
import org.example.movieservice.repository.ScreeningRepository;
import org.example.movieservice.service.MovieService;
import org.example.movieservice.service.ScreeningService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScreeningServiceImpl implements ScreeningService {
    private final MovieService movieService;
    private final ScreeningRepository screeningRepository;
    private final ScreeningMapper screeningMapper;

    @Value("${time.clean.screening}")
    private int timeCleanScreening;

    @Override
    public ScreeningResponse createScreening(ScreeningCreateRequest screeningCreateRequest, UUID movieId) {
        Movie movie = movieService.findById(movieId);
        Instant start = screeningCreateRequest.startsAt();
        Instant end = start.plus(Duration.ofMinutes(movie.getDurationMinutes()));
        // some time for cleaning cinema
        Instant startWithBuffer = start.minus(Duration.ofMinutes(timeCleanScreening));
        Instant endWithBuffer = end.plus(Duration.ofMinutes(timeCleanScreening));
        // check overlapping screening with others screening
        if (screeningRepository.existsOverlapping(screeningCreateRequest.hall(), startWithBuffer, endWithBuffer)) {
            throw new CustomException(ErrorMessages.SCREENING_OVERLAP);
        }

        Screening screening = Screening.builder()
                .movie(movie)
                .hall(screeningCreateRequest.hall())
                .startsAt(start)
                .endsAt(end)
                .totalSeats(screeningCreateRequest.totalSeats())
                .availableSeats(screeningCreateRequest.totalSeats())
                .price(screeningCreateRequest.price())
                .build();

        Screening save = screeningRepository.save(screening);

        return screeningMapper.toScreeningResponse(save);
    }
    @Override
    public List<ScreeningResponse> getAllScreeningByMovieId(UUID movieId) {
        return screeningRepository.findAllByMovieId(movieId)
                .stream()
                .map(screeningMapper::toScreeningResponse)
                .toList();
    }

    @Override
    public ScreeningResponse getScreening(UUID id) {
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorMessages.SCREENING_DOESNT_EXISTS));

        return screeningMapper.toScreeningResponse(screening);
    }

    @Override
    public List<ScreeningResponse> getAllScreening() {
        return screeningRepository.findAll()
                .stream()
                .map(screeningMapper::toScreeningResponse)
                .toList();
    }
}
