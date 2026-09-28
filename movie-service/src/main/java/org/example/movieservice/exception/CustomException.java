package org.example.movieservice.exception;

public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}