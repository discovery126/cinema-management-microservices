package com.github.discovery126.authservice.util;

import com.github.discovery126.authservice.model.Role;
import com.github.discovery126.authservice.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private Duration accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Duration refreshTokenExpiration;

    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenExpiration, "access");
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenExpiration, "refresh");
    }

    private String generateToken(User user, Duration expiration, String type) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .claim("type", type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    private void extractAllClaims(String token) {
        Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}