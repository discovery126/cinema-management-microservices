package org.example.movieservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.movieservice.dto.request.CreateGenresRequest;
import org.example.movieservice.dto.response.GenreResponse;
import org.example.movieservice.exception.GenreNotFoundException;
import org.example.movieservice.model.Genre;
import org.example.movieservice.repository.GenreRepository;
import org.example.movieservice.service.GenreService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    //create only those genres that are not in the database
    //IDEMPOTENT operation
    @Override
    @Transactional
    public void createGenres(CreateGenresRequest req) {
        Set<String> names = new HashSet<>(req.genres());
        Set<String> existing = genreRepository.findExistingNames(names);

        List<Genre> toSave = names.stream()
                .filter(name -> !existing.contains(name))
                .map(name -> Genre.builder()
                        .name(name)
                        .build())
                .toList();

        if (!toSave.isEmpty()) {
            genreRepository.saveAll(toSave);
        }
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
    @Override
    public Set<Genre> findAllById(Set<UUID> ids) {
        Set<Genre> found = new HashSet<>(genreRepository.findAllById(ids));
        if (found.size() != ids.size()) {
            Set<UUID> foundIds = found.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            Set<UUID> notFound = new HashSet<>(ids);
            notFound.removeAll(foundIds);
            throw new GenreNotFoundException(notFound);
        }
        return found;
    }
}
