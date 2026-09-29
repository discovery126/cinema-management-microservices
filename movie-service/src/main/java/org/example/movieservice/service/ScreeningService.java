package org.example.movieservice.service;

import org.example.movieservice.dto.request.ScreeningCreateRequest;
import org.example.movieservice.dto.response.ScreeningResponse;

import java.util.List;
import java.util.UUID;

public interface ScreeningService {
    ScreeningResponse createScreening(ScreeningCreateRequest screeningCreateRequest, UUID movieId);
    List<ScreeningResponse> getAllScreeningByMovieId(UUID movieId);
    ScreeningResponse getScreening(UUID id);
    List<ScreeningResponse> getAllScreening();
}
