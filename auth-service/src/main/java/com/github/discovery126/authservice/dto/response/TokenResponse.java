package com.github.discovery126.authservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(@JsonProperty(value = "access_token")
                            String accessToken,
                            @JsonProperty(value = "refresh_token")
                            String refreshToken
) {}