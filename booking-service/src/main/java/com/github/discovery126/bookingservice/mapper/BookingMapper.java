package com.github.discovery126.bookingservice.mapper;

import com.github.discovery126.bookingservice.dto.response.BookingResponse;
import com.github.discovery126.bookingservice.model.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponse toBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .screeningId(booking.getScreeningId())
                .customerName(booking.getCustomerName())
                .seatsCount(booking.getSeatsCount())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
