package com.github.discovery126.movieservice.controller;

import lombok.RequiredArgsConstructor;
import com.github.discovery126.movieservice.dto.response.ScreeningResponse;
import com.github.discovery126.movieservice.service.ScreeningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/{id}/reserve")
    public ResponseEntity<ScreeningResponse> reserve(@PathVariable UUID id,
                                                     @RequestParam(name = "seatsCount") Integer seatsCount) {
        screeningService.reserve(id,seatsCount);
        return ResponseEntity.ok()
                .build();
    }
    @PostMapping("/{id}/release")
    public ResponseEntity<ScreeningResponse> release(@PathVariable UUID id,
                                                     @RequestParam(name = "seatsCount") Integer seatsCount) {
        screeningService.release(id,seatsCount);
        return ResponseEntity.ok()
                .build();
    }
}
