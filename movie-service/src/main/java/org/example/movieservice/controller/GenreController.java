package org.example.movieservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.movieservice.dto.request.CreateGenresRequest;
import org.example.movieservice.dto.response.GenreResponse;
import org.example.movieservice.service.GenreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    public ResponseEntity<Void> createGenres(@RequestBody @Valid CreateGenresRequest createGenresRequestList) {
        genreService.createGenres(createGenresRequestList);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
    @GetMapping
    public ResponseEntity<List<GenreResponse>> getGenres() {

        return ResponseEntity
                .ok(genreService.getGenres());
    }
}
