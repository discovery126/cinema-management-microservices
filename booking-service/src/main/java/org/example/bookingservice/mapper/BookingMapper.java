package org.example.bookingservice.mapper;

import org.example.bookingservice.dto.response.BookingResponse;
import org.example.bookingservice.model.Booking;
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
