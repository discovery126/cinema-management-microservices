package com.github.discovery126.bookingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ProblemDetail handleBadRequest(CustomException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ScreeningNotFoundException.class)
    public ProblemDetail handleNotFound(ScreeningNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(BookingConflictException.class)
    public ProblemDetail handle(BookingConflictException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handle(ResourceAccessException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE, "External service unavailable");
        pd.setTitle("Service unavailable");
        return pd;
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ProblemDetail handle(RestClientResponseException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.valueOf(ex.getStatusCode().value()),
                "External service error: " + ex.getStatusText());
        pd.setTitle("External service error");
        return pd;
    }
}