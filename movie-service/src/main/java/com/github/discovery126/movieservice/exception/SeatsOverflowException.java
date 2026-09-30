package com.github.discovery126.movieservice.exception;

public class SeatsOverflowException extends RuntimeException {
    public SeatsOverflowException(String message) {
        super(message);
    }
}
