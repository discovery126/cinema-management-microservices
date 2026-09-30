package com.github.discovery126.bookingservice.controller;

import lombok.RequiredArgsConstructor;
import com.github.discovery126.bookingservice.dto.request.BookingCreateRequest;
import com.github.discovery126.bookingservice.dto.response.BookingResponse;
import com.github.discovery126.bookingservice.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingCreateRequest bookingCreateRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.createBooking(bookingCreateRequest));

    }
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable UUID id) {
        return ResponseEntity
                .ok(bookingService.getBooking(id));

    }
    @PostMapping("/{id}/release")
    public ResponseEntity<BookingResponse> release(@PathVariable UUID id) {
        bookingService.release(id);
        return ResponseEntity
                .ok()
                .build();

    }
}
