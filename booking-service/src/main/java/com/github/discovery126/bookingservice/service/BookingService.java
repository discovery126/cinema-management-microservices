package com.github.discovery126.bookingservice.service;

import com.github.discovery126.bookingservice.dto.request.BookingCreateRequest;
import com.github.discovery126.bookingservice.dto.response.BookingResponse;

import java.util.UUID;

public interface BookingService {
    BookingResponse createBooking(BookingCreateRequest bookingCreateRequest);
    void completedBooking(UUID bookingId);
    BookingResponse getBooking(UUID id);
    void release(UUID bookingId);
}
