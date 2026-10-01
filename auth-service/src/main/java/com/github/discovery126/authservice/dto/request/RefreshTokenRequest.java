package com.github.discovery126.authservice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token must not be blank")
        @JsonProperty(value = "refresh_token")
        String refreshToken
) {}