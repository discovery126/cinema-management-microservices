package org.example.movieservice.exception;

public final class ErrorMessages {

    public static final String MOVIE_ALREADY_EXISTS = "movie already exists";
    public static final String MOVIE_DOESNT_EXISTS = "movie doesn't exists";
    public static final String SCREENING_OVERLAP = "screening overlaps with existing one";
    public static final String SCREENING_DOESNT_EXISTS = "screening doesn't exists";

    private ErrorMessages() {
        throw new AssertionError("No instances");
    }
}