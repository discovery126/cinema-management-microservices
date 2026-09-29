package org.example.movieservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.movieservice.dto.response.ScreeningResponse;
import org.example.movieservice.service.ScreeningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/screenings")
public class ScreeningController {

    private final ScreeningService screeningService;

    @GetMapping
    public ResponseEntity<List<ScreeningResponse>> getAllScreening() {
        return ResponseEntity
                .ok(screeningService.getAllScreening());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScreeningResponse> getScreening(@PathVariable UUID id) {
        return ResponseEntity
                .ok(screeningService.getScreening(id));
    }
}
