package com.github.discovery126.bookingservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.github.discovery126.bookingservice.dto.payment.PaymentDto;
import com.github.discovery126.bookingservice.dto.request.BookingCreateRequest;
import com.github.discovery126.bookingservice.dto.response.BookingResponse;
import com.github.discovery126.bookingservice.dto.movie.ScreeningDto;
import com.github.discovery126.bookingservice.exception.BookingConflictException;
import com.github.discovery126.bookingservice.exception.CustomException;
import com.github.discovery126.bookingservice.exception.ErrorMessages;
import com.github.discovery126.bookingservice.exception.ScreeningNotFoundException;
import com.github.discovery126.bookingservice.mapper.BookingMapper;
import com.github.discovery126.bookingservice.model.Booking;
import com.github.discovery126.bookingservice.model.BookingStatus;
import com.github.discovery126.bookingservice.repository.BookingRepository;
import com.github.discovery126.bookingservice.service.BookingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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

    private final KafkaTemplate<String, PaymentDto> kafkaTemplate;

    private static final String SCREENING_BY_ID = "/screenings/{id}";
    private static final String RESERVE_PATH = "/screenings/{id}/reserve";
    private static final String RELEASE_PATH = "/screenings/{id}/release";

    @Value("${kafka.topics.booking-created}")
    private String bookingCreatedTopic;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingCreateRequest request) {
        log.info("Creating booking: screeningId={}, customer='{}', seats={}",
                request.screeningId(), request.customerName(), request.seatsCount());

        ScreeningDto screeningDto = fetchScreening(request.screeningId());
        reserveSeats(request.screeningId(), request.seatsCount());
        log.info("Seats reserved: screeningId={}, seats={}", request.screeningId(), request.seatsCount());

        try {
            BigDecimal totalPrice = screeningDto.price()
                    .multiply(BigDecimal.valueOf(request.seatsCount()));

            Booking booking = Booking.builder()
                    .screeningId(screeningDto.id())
                    .customerName(request.customerName())
                    .seatsCount(request.seatsCount())
                    .totalPrice(totalPrice)
                    .status(BookingStatus.PENDING_PAYMENT)
                    .createdAt(Instant.now())
                    .build();

            Booking save = bookingRepository.save(booking);
            log.info("Booking created: id={}, screeningId={}, seats={}, totalPrice={}",
                    save.getId(), save.getScreeningId(), save.getSeatsCount(), save.getTotalPrice());
            kafkaTemplate.send(bookingCreatedTopic,new PaymentDto(save.getId(),save.getTotalPrice()));
            log.info("Payment event sent: bookingId={}, amount={}", save.getId(), save.getTotalPrice());
            return bookingMapper.toBookingResponse(save);
        } catch (Exception e) {
            log.error("Booking creation failed ({}), releasing seats: screeningId={}, seats={}",
                    e.getClass().getSimpleName(),
                    request.screeningId(), request.seatsCount(), e);
            try {
                releaseSeats(request.screeningId(), request.seatsCount());
                log.info("Seats released after failure: screeningId={}, seats={}",
                        request.screeningId(), request.seatsCount());
            } catch (Exception releaseEx) {
                log.error("Failed to release seats after booking failure: screeningId={}, seats={}",
                        request.screeningId(), request.seatsCount(), releaseEx);
            }
            throw e;
        }
    }

    @Override
    @Transactional
    @KafkaListener(topics = "${kafka.topics.payment-completed}",
                    groupId = "booking-service-payment-completed")
    public void completedBooking(UUID bookingId) {
        log.info("Completing booking id={}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Booking id={} not found", bookingId);
                    return new CustomException(ErrorMessages.BOOKING_DOESNT_EXISTS);
                });
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            log.info("Booking id={} already confirmed, skipping", bookingId);
            return;
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        log.info("Booking id={} confirmed", bookingId);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Booking id={} not found", id);
                    return new CustomException(ErrorMessages.BOOKING_DOESNT_EXISTS);
                });
        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    @Transactional
    @KafkaListener(topics = "${kafka.topics.payment-failed}"
                    ,groupId = "booking-service-payment-failed")
    public void release(UUID bookingId) {
        log.info("Releasing booking id={}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Cannot release: booking id={} not found", bookingId);
                    return new CustomException(ErrorMessages.BOOKING_DOESNT_EXISTS);
                });

        if (booking.getStatus() == BookingStatus.CANCELED) {
            log.info("Booking id={} already canceled, skipping release", bookingId);
            return;
        }

        releaseSeats(booking.getScreeningId(), booking.getSeatsCount());
        booking.setStatus(BookingStatus.CANCELED);

        log.info("Booking id={} canceled: screeningId={}, seats={} released",
                bookingId, booking.getScreeningId(), booking.getSeatsCount());
    }

    private ScreeningDto fetchScreening(UUID id) {
        try {
            ScreeningDto screening = restClient.get()
                    .uri(SCREENING_BY_ID, id)
                    .retrieve()
                    .body(ScreeningDto.class);
            log.debug("Fetched screening id={}: hall={}, price={}",
                    id, screening.hall(), screening.price());
            return screening;
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Screening id={} not found in movie-service", id);
            throw new ScreeningNotFoundException(ErrorMessages.SCREENING_DOESNT_EXISTS);
        } catch (HttpServerErrorException e) {
            log.error("movie-service returned {} for screening id={}: {}",
                    e.getStatusCode(), id, e.getResponseBodyAsString(), e);
            throw e;
        } catch (RestClientException e) {
            log.error("Failed to call movie-service for screening id={}: {}",
                    id, e.getMessage(), e);
            throw e;
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
            log.debug("Seats reserved: screeningId={}, seats={}", screeningId, seatsCount);
        } catch (HttpClientErrorException.Conflict e) {
            log.warn("Reserve conflict: screeningId={}, seats={} - sold out", screeningId, seatsCount);
            throw new BookingConflictException(ErrorMessages.SOLD_OUT_SCREENING);
        } catch (HttpServerErrorException e) {
            log.error("movie-service returned {} on reserve: screeningId={}, seats={}: {}",
                    e.getStatusCode(), screeningId, seatsCount, e.getResponseBodyAsString(), e);
            throw e;
        } catch (RestClientException e) {
            log.error("Failed to reserve seats via movie-service: screeningId={}, seats={}: {}",
                    screeningId, seatsCount, e.getMessage(), e);
            throw e;
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
            log.debug("Seats released: screeningId={}, seats={}", screeningId, seatsCount);
        } catch (HttpClientErrorException.Conflict e) {
            log.warn("Release conflict: screeningId={}, seats={} - overflow", screeningId, seatsCount);
            throw new BookingConflictException(ErrorMessages.SEATS_OVERFLOW);
        } catch (HttpServerErrorException e) {
            log.error("movie-service returned {} on release: screeningId={}, seats={}: {}",
                    e.getStatusCode(), screeningId, seatsCount, e.getResponseBodyAsString(), e);
            throw e;
        } catch (RestClientException e) {
            log.error("Failed to release seats via movie-service: screeningId={}, seats={}: {}",
                    screeningId, seatsCount, e.getMessage(), e);
            throw e;
        }
    }
}