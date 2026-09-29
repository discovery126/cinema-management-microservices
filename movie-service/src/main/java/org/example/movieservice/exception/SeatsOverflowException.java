package org.example.movieservice.exception;

public class SeatsOverflowException extends RuntimeException {
    public SeatsOverflowException(String message) {
        super(message);
    }
}
