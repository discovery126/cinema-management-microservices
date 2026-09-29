package org.example.bookingservice.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservice.dto.request.BookingCreateRequest;
import org.example.bookingservice.dto.response.BookingResponse;
import org.example.bookingservice.dto.movie.ScreeningDto;
import org.example.bookingservice.exception.BookingConflictException;
import org.example.bookingservice.exception.CustomException;
import org.example.bookingservice.exception.ErrorMessages;
import org.example.bookingservice.exception.ScreeningNotFoundException;
import org.example.bookingservice.mapper.BookingMapper;
import org.example.bookingservice.model.Booking;
import org.example.bookingservice.model.BookingStatus;
import org.example.bookingservice.repository.BookingRepository;
import org.example.bookingservice.service.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final RestClient restClient;

    private static final String SCREENING_BY_ID = "/screenings/{id}";
    private static final String RESERVE_PATH = "/screenings/{id}/reserve";
    private static final String RELEASE_PATH = "/screenings/{id}/release";

    @Override
    @Transactional
    public BookingResponse createBooking(BookingCreateRequest request) {
        ScreeningDto screeningDto = fetchScreening(request.screeningId());
        reserveSeats(request.screeningId(), request.seatsCount());

        try {
            Booking booking = Booking.builder()
                    .screeningId(screeningDto.id())
                    .customerName(request.customerName())
                    .seatsCount(request.seatsCount())
                    .totalPrice(screeningDto.price()
                            .multiply(BigDecimal.valueOf(request.seatsCount())))
                    .status(BookingStatus.PENDING_PAYMENT)
                    .createdAt(Instant.now())
                    .build();

            Booking save = bookingRepository.save(booking);
            return bookingMapper.toBookingResponse(save);
        } catch (Exception e) {
            try {
                releaseSeats(request.screeningId(), request.seatsCount());
            } catch (Exception releaseEx) {
                log.error("Failed to release seats after booking failure: screeningId={}, seats={}",
                        request.screeningId(), request.seatsCount(), releaseEx);
            }
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorMessages.BOOKING_DOESNT_EXISTS));
        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    @Transactional
    public void release(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CustomException(ErrorMessages.BOOKING_DOESNT_EXISTS));

        if (booking.getStatus() == BookingStatus.CANCELED) {
            return;
        }

        releaseSeats(booking.getScreeningId(), booking.getSeatsCount());
        booking.setStatus(BookingStatus.CANCELED);
    }

    private ScreeningDto fetchScreening(UUID id) {
        try {
            return restClient.get()
                    .uri(SCREENING_BY_ID, id)
                    .retrieve()
                    .body(ScreeningDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ScreeningNotFoundException(ErrorMessages.SCREENING_DOESNT_EXISTS);
        }
    }

    private void reserveSeats(UUID screeningId, Integer seatsCount) {
        try {
            restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(RESERVE_PATH)
                            .queryParam("seatsCount", seatsCount)
                            .build(screeningId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict e) {
            throw new BookingConflictException(ErrorMessages.SOLD_OUT_SCREENING);
        }
    }

    private void releaseSeats(UUID screeningId, Integer seatsCount) {
        try {
            restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(RELEASE_PATH)
                            .queryParam("seatsCount", seatsCount)
                            .build(screeningId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict e) {
            throw new BookingConflictException(ErrorMessages.SEATS_OVERFLOW);
        }
    }
}