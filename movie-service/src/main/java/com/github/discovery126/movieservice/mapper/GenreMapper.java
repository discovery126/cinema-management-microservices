package com.github.discovery126.movieservice.mapper;

import com.github.discovery126.movieservice.dto.response.GenreResponse;
import com.github.discovery126.movieservice.model.Genre;
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
