package com.github.discovery126.authservice.service;

import com.github.discovery126.authservice.dto.request.LoginRequest;
import com.github.discovery126.authservice.dto.request.RefreshTokenRequest;
import com.github.discovery126.authservice.dto.request.RegisterRequest;
import com.github.discovery126.authservice.dto.response.TokenResponse;

public interface AuthService {
    void register(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    TokenResponse refresh(RefreshTokenRequest request);
    void logout(RefreshTokenRequest request);

}