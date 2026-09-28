package org.example.movieservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.movieservice.dto.request.CreateMovieRequest;
import org.example.movieservice.dto.response.MovieResponse;
import org.example.movieservice.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<MovieResponse> createMovie(@RequestBody CreateMovieRequest createMovieRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(movieService.createMovie(createMovieRequest));
    }
    @GetMapping
    public ResponseEntity<List<MovieResponse>> getMovies() {
        return org.springframework.http
                .ResponseEntity
                .ok(movieService.getMovies());
    }
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovie(@PathVariable UUID id) {
        return ResponseEntity
                .ok(movieService.getMovie(id));
    }
}
