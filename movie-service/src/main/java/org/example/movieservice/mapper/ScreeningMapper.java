package org.example.movieservice.mapper;

import org.example.movieservice.dto.response.MovieResponse;
import org.example.movieservice.dto.response.ScreeningResponse;
import org.example.movieservice.model.Movie;
import org.example.movieservice.model.Screening;
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
