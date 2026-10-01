package com.github.discovery126.bookingservice.service.impl;

import com.github.discovery126.bookingservice.dto.auth.LoginRequest;
import com.github.discovery126.bookingservice.dto.auth.TokenResponse;
import com.github.discovery126.bookingservice.exception.CustomException;
import com.github.discovery126.bookingservice.service.TokenCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class TokenCacheServiceImpl implements TokenCacheService {

    private final RestClient restClientAuth;
    private final ReentrantLock lock = new ReentrantLock();

    @Value("${app.booking.email}")
    private String email;

    @Value("${app.booking.password}")
    private String password;

    private String cachedToken;
    private Instant expiresAt = Instant.MIN;
    private final static String authLogin = "/auth/login";

    @Override
    public String getToken() {
        if (cachedToken != null && Instant.now().isBefore(expiresAt)) {
            return cachedToken;
        }

        lock.lock();
        try {
            if (cachedToken != null && Instant.now().isBefore(expiresAt)) {
                return cachedToken;
            }
            return refreshToken();
        } finally {
            lock.unlock();
        }
    }

    private String refreshToken() {
        TokenResponse response = restClientAuth.post()
                .uri(authLogin)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest(email, password))
                .retrieve()
                .body(TokenResponse.class);

        if (response == null || response.accessToken() == null) {
            throw new CustomException("Auth service returned invalid response");
        }

        cachedToken = response.accessToken();
        expiresAt = Instant.now().plus(Duration.ofMinutes(15));

        return cachedToken;
    }

//    private Instant extractExpiration(String token) {
//        Claims claims = Jwts.parser()
//                .verifyWith(getSigningKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//        return claims.getExpiration().toInstant();
//    }
//
//    private SecretKey getSigningKey() {
//        byte[] keyBytes = Decoders.BASE64URL.decode(secret);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
}