package org.example.movieservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.movieservice.dto.request.CreateGenresRequest;
import org.example.movieservice.dto.request.CreateMovieRequest;
import org.example.movieservice.dto.response.GenreResponse;
import org.example.movieservice.dto.response.MovieResponse;
import org.example.movieservice.exception.CustomException;
import org.example.movieservice.exception.ErrorMessages;
import org.example.movieservice.model.Genre;
import org.example.movieservice.repository.GenreRepository;
import org.example.movieservice.service.GenreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public void createGenres(CreateGenresRequest createGenresRequestList) {
        List<Genre> genreList = createGenresRequestList.genres()
                .stream()
                .map(name -> Genre.builder()
                        .name(name)
                        .build())
                .toList();
        boolean genresExists = genreRepository.existsByNameIn(genreList
                .stream()
                .map(Genre::getName)
                .toList()
        );
        if (genresExists) {
            throw new CustomException(ErrorMessages.GENRE_ALREADY_EXISTS);
        }
        genreRepository.saveAll(genreList);
    }

    @Override
    public List<GenreResponse> getGenres() {
        return genreRepository.findAll()
                .stream()
                .map(genre -> GenreResponse.builder()
                        .id(genre.getId())
                        .name(genre.getName())
                        .build())
                .toList();
    }
}
