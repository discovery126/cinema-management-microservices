package com.github.discovery126.movieservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;                          // ← добавили
import com.github.discovery126.movieservice.dto.request.CreateGenresRequest;
import com.github.discovery126.movieservice.dto.response.GenreResponse;
import com.github.discovery126.movieservice.exception.GenreNotFoundException;
import com.github.discovery126.movieservice.mapper.GenreMapper;
import com.github.discovery126.movieservice.model.Genre;
import com.github.discovery126.movieservice.repository.GenreRepository;
import com.github.discovery126.movieservice.service.GenreService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    //create only those genres that are not in the database
    //IDEMPOTENT operation
    @Override
    @Transactional
    public void createGenres(CreateGenresRequest req) {
        Set<String> names = new HashSet<>(req.genres());
        log.info("Creating genres: requested={}, unique={}", req.genres().size(), names.size());

        Set<String> existing = genreRepository.findExistingNames(names);
        log.debug("Genres already in DB: {}", existing);

        List<Genre> toSave = names.stream()
                .filter(name -> !existing.contains(name))
                .map(name -> Genre.builder()
                        .name(name)
                        .build())
                .toList();

        if (toSave.isEmpty()) {
            log.info("No new genres to create, all {} already exist", names.size());
            return;
        }

        genreRepository.saveAll(toSave);
        log.info("Created {} new genres: {}", toSave.size(),
                toSave.stream().map(Genre::getName).toList());
    }

    @Override
    public List<GenreResponse> getGenres() {
        log.debug("Fetching all genres");
        List<GenreResponse> genres = genreRepository.findAll()
                .stream()
                .map(genreMapper::toGenreResponse)
                .toList();
        log.debug("Found {} genres", genres.size());
        return genres;
    }

    @Override
    public Set<Genre> findAllById(Set<UUID> ids) {
        log.debug("Finding genres by ids: {}", ids);
        Set<Genre> found = new HashSet<>(genreRepository.findAllById(ids));

        if (found.size() != ids.size()) {
            Set<UUID> foundIds = found.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            Set<UUID> notFound = new HashSet<>(ids);
            notFound.removeAll(foundIds);
            log.warn("Genres not found by ids: {}", notFound);
            throw new GenreNotFoundException(notFound);
        }

        log.debug("Found all {} genres", found.size());
        return found;
    }
}