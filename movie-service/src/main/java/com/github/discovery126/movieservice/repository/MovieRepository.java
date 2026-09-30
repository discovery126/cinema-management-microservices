package com.github.discovery126.movieservice.repository;

import com.github.discovery126.movieservice.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    boolean existsByTitle(String title);
}
