package com.github.discovery126.movieservice.repository;

import com.github.discovery126.movieservice.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
    @Query("SELECT g.name FROM Genre g WHERE g.name IN :names")
    Set<String> findExistingNames(@Param("names") Set<String> names);
}
