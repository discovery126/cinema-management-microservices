package org.example.movieservice.mapper;

import org.example.movieservice.dto.response.GenreResponse;
import org.example.movieservice.model.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public GenreResponse toGenreResponse(Genre genre) {
        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }
}
