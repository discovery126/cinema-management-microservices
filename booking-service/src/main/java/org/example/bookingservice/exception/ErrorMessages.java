package org.example.bookingservice.exception;

public final class ErrorMessages {

    public static final String BAD_REQUEST_FROM_SERVER = "bad request from server";
    public static final String BOOKING_DOESNT_EXISTS = "booking doesn't exists";
    public static final String SOLD_OUT_SCREENING = "sold out screening";
    public static final String SCREENING_DOESNT_EXISTS = "screening doesn't exists";
    public static final String SEATS_OVERFLOW = "seats count overflow";

    private ErrorMessages() {
        throw new AssertionError("No instances");
    }
}

