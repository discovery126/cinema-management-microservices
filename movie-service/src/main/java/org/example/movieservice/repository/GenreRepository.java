package org.example.movieservice.repository;

import org.example.movieservice.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
    boolean existsByNameIn(Collection<String> names);
}
