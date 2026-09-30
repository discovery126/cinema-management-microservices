package com.github.discovery126.movieservice.mapper;

import com.github.discovery126.movieservice.dto.response.MovieResponse;
import com.github.discovery126.movieservice.model.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {
    public MovieResponse toMovieResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genres(movie.getGenres())
                .durationMinutes(movie.getDurationMinutes())
                .build();
    }
}
