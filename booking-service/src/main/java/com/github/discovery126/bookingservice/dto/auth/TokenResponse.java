package com.github.discovery126.bookingservice.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        @JsonProperty(value = "access_token")
        String accessToken,
        @JsonProperty(value = "refresh_token")
        String refreshToken
) {}
