package org.example.bookingservice.service;

import org.example.bookingservice.dto.request.BookingCreateRequest;
import org.example.bookingservice.dto.response.BookingResponse;

import java.util.UUID;

public interface BookingService {
    BookingResponse createBooking(BookingCreateRequest bookingCreateRequest);
    BookingResponse getBooking(UUID id);
    void release(UUID bookingId);
}
