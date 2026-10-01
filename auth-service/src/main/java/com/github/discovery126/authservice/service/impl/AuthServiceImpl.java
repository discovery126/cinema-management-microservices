package com.github.discovery126.authservice.service.impl;

import com.github.discovery126.authservice.dto.request.LoginRequest;
import com.github.discovery126.authservice.dto.request.RefreshTokenRequest;
import com.github.discovery126.authservice.dto.request.RegisterRequest;
import com.github.discovery126.authservice.dto.response.TokenResponse;
import com.github.discovery126.authservice.exception.CustomException;
import com.github.discovery126.authservice.exception.ErrorMessages;
import com.github.discovery126.authservice.model.RefreshToken;
import com.github.discovery126.authservice.model.Role;
import com.github.discovery126.authservice.model.User;
import com.github.discovery126.authservice.repository.RefreshTokenRepository;
import com.github.discovery126.authservice.repository.RoleRepository;
import com.github.discovery126.authservice.repository.UserRepository;
import com.github.discovery126.authservice.service.AuthService;
import com.github.discovery126.authservice.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE = "USER";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${jwt.refresh-token-expiration}")
    private Duration refreshTokenExpiration;

    @Override
    @Transactional
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }

        Role role = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new CustomException(ErrorMessages.ROLE_NOT_FOUND));

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .created(Instant.now())
                .roles(Set.of(role))
                .build();

        userRepository.save(user);
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorMessages.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(ErrorMessages.INVALID_CREDENTIALS);
        }

        refreshTokenRepository.revokeAllByUserId(user.getId(), Instant.now(), "new_login");

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiresAt(Instant.now().plus(refreshTokenExpiration))
                .revoked(false)
                .build());

        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {

        if (!jwtService.isTokenValid(request.refreshToken())) {
            throw new CustomException(ErrorMessages.INVALID_REFRESH_TOKEN);
        }

        RefreshToken stored = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new CustomException(ErrorMessages.INVALID_REFRESH_TOKEN));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new CustomException(ErrorMessages.INVALID_REFRESH_TOKEN);
        }

        User user = stored.getUser();

        stored.setRevoked(true);
        stored.setRevokedAt(Instant.now());
        stored.setRevokeReason("rotation");
        refreshTokenRepository.save(stored);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        refreshTokenRepository.save(RefreshToken.builder()
                .token(newRefreshToken)
                .user(user)
                .expiresAt(Instant.now().plus(refreshTokenExpiration))
                .revoked(false)
                .build());

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.findByToken(request.refreshToken())
                .ifPresent(token -> {
                    token.setRevoked(true);
                    token.setRevokedAt(Instant.now());
                    token.setRevokeReason("logout");
                    refreshTokenRepository.save(token);
                });
    }
}