package com.github.discovery126.authservice.exception;

public final class ErrorMessages {
    public static final String EMAIL_ALREADY_EXISTS = "email already exists";
    public static final String INVALID_CREDENTIALS = "invalid credentials";
    public static final String ROLE_NOT_FOUND = "role not found";
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";

    private ErrorMessages() {
        throw new AssertionError("No instances");
    }
}