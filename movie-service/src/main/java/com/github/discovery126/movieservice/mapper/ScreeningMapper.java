package com.github.discovery126.movieservice.mapper;

import com.github.discovery126.movieservice.dto.response.MovieResponse;
import com.github.discovery126.movieservice.dto.response.ScreeningResponse;
import com.github.discovery126.movieservice.model.Movie;
import com.github.discovery126.movieservice.model.Screening;
import org.springframework.stereotype.Component;

@Component
public class ScreeningMapper {

    public ScreeningResponse toScreeningResponse(Screening screening) {
        Movie movie = screening.getMovie();

        MovieResponse movieResponse = MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genres(movie.getGenres())
                .durationMinutes(movie.getDurationMinutes())
                .build();

        return ScreeningResponse.builder().id(screening.getId())
                .movieResponse(movieResponse)
                .hall(screening.getHall())
                .totalSeats(screening.getTotalSeats())
                .availableSeats(screening.getTotalSeats())
                .price(screening.getPrice())
                .startsAt(screening.getStartsAt())
                .endsAt(screening.getEndsAt())
                .build();

    }

}
