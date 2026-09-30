package com.github.discovery126.movieservice.service;

import com.github.discovery126.movieservice.dto.request.ScreeningCreateRequest;
import com.github.discovery126.movieservice.dto.response.ScreeningResponse;

import java.util.List;
import java.util.UUID;

public interface ScreeningService {
    ScreeningResponse createScreening(ScreeningCreateRequest screeningCreateRequest, UUID movieId);
    List<ScreeningResponse> getAllScreeningByMovieId(UUID movieId);
    ScreeningResponse getScreening(UUID id);
    List<ScreeningResponse> getAllScreening();
    void reserve(UUID id, Integer seatsCount);
    void release(UUID id, Integer seatsCount);
}
