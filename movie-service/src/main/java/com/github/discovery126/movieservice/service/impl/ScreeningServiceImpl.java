package com.github.discovery126.movieservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;                          // ← добавили
import com.github.discovery126.movieservice.dto.request.ScreeningCreateRequest;
import com.github.discovery126.movieservice.dto.response.ScreeningResponse;
import com.github.discovery126.movieservice.exception.CustomException;
import com.github.discovery126.movieservice.exception.ErrorMessages;
import com.github.discovery126.movieservice.exception.SeatsOverflowException;
import com.github.discovery126.movieservice.exception.SoldOutException;
import com.github.discovery126.movieservice.mapper.ScreeningMapper;
import com.github.discovery126.movieservice.model.Movie;
import com.github.discovery126.movieservice.model.Screening;
import com.github.discovery126.movieservice.repository.ScreeningRepository;
import com.github.discovery126.movieservice.service.MovieService;
import com.github.discovery126.movieservice.service.ScreeningService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreeningServiceImpl implements ScreeningService {
    private final MovieService movieService;
    private final ScreeningRepository screeningRepository;
    private final ScreeningMapper screeningMapper;

    @Value("${time.clean.screening}")
    private int timeCleanScreening;

    @Override
    @Transactional
    public ScreeningResponse createScreening(ScreeningCreateRequest screeningCreateRequest, UUID movieId) {
        log.info("Creating screening: movieId={}, hall={}, startsAt={}, seats={}, price={}",
                movieId,
                screeningCreateRequest.hall(),
                screeningCreateRequest.startsAt(),
                screeningCreateRequest.totalSeats(),
                screeningCreateRequest.price());

        Movie movie = movieService.findById(movieId);
        Instant start = screeningCreateRequest.startsAt();
        Instant end = start.plus(Duration.ofMinutes(movie.getDurationMinutes()));

        // some time for cleaning cinema
        Instant startWithBuffer = start.minus(Duration.ofMinutes(timeCleanScreening));
        Instant endWithBuffer = end.plus(Duration.ofMinutes(timeCleanScreening));

        log.debug("Screening time window: start={}, end={}, with buffer: {}-{}",
                start, end, startWithBuffer, endWithBuffer);

        // check overlapping screening with others screening
        if (screeningRepository.existsOverlapping(screeningCreateRequest.hall(), startWithBuffer, endWithBuffer)) {
            log.warn("Screening overlap: movieId={}, hall={}, startsAt={}",
                    movieId, screeningCreateRequest.hall(), start);
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

        log.info("Screening created: id={}, movieId={}, hall={}, startsAt={}",
                save.getId(), movieId, save.getHall(), save.getStartsAt());
        return screeningMapper.toScreeningResponse(save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningResponse> getAllScreeningByMovieId(UUID movieId) {
        log.debug("Fetching screenings for movieId={}", movieId);
        List<ScreeningResponse> screenings = screeningRepository.findAllByMovieId(movieId)
                .stream()
                .map(screeningMapper::toScreeningResponse)
                .toList();
        log.debug("Found {} screenings for movieId={}", screenings.size(), movieId);
        return screenings;
    }

    @Override
    @Transactional(readOnly = true)
    public ScreeningResponse getScreening(UUID id) {
        log.debug("Fetching screening id={}", id);
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Screening id={} not found", id);
                    return new CustomException(ErrorMessages.SCREENING_DOESNT_EXISTS);
                });
        log.debug("Screening id={} found: movieId={}, startsAt={}",
                id, screening.getMovie().getId(), screening.getStartsAt());
        return screeningMapper.toScreeningResponse(screening);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreeningResponse> getAllScreening() {
        log.debug("Fetching all screenings");
        List<ScreeningResponse> screenings = screeningRepository.findAll()
                .stream()
                .map(screeningMapper::toScreeningResponse)
                .toList();
        log.debug("Found {} screenings", screenings.size());
        return screenings;
    }

    @Override
    @Transactional
    public void reserve(UUID id, Integer seatsCount) {
        log.info("Reserving {} seats for screening id={}", seatsCount, id);

        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot reserve: screening id={} not found", id);
                    return new CustomException(ErrorMessages.SCREENING_DOESNT_EXISTS);
                });

        int available = screening.getAvailableSeats();
        if (available < seatsCount) {
            log.warn("Cannot reserve {} seats for screening id={}: only {} available",
                    seatsCount, id, available);
            throw new SoldOutException(ErrorMessages.SOLD_OUT_SCREENING);
        }

        screening.setAvailableSeats(available - seatsCount);
        log.info("Reserved {} seats for screening id={}: available {} -> {}",
                seatsCount, id, available, screening.getAvailableSeats());
    }

    @Override
    @Transactional
    public void release(UUID id, Integer seatsCount) {
        log.info("Releasing {} seats for screening id={}", seatsCount, id);

        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot release: screening id={} not found", id);
                    return new CustomException(ErrorMessages.SCREENING_DOESNT_EXISTS);
                });

        int available = screening.getAvailableSeats();
        int newAvailable = available + seatsCount;

        if (newAvailable > screening.getTotalSeats()) {
            log.warn("Cannot release {} seats for screening id={}: would exceed total ({} > {})",
                    seatsCount, id, newAvailable, screening.getTotalSeats());
            throw new SeatsOverflowException(ErrorMessages.SEATS_OVERFLOW);
        }

        screening.setAvailableSeats(newAvailable);
        log.info("Released {} seats for screening id={}: available {} -> {}",
                seatsCount, id, available, newAvailable);
    }
}