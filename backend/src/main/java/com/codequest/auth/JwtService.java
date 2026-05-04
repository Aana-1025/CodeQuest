package com.codequest.auth;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.common.security.CurrentUserPrincipal;
import com.codequest.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final Key signingKey;
    private final int accessMinutes;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-minutes}") int accessMinutes) {
        if (secret == null || secret.trim().length() < 32) {
            throw new IllegalArgumentException("JWT secret must be set and at least 32 characters.");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessMinutes = accessMinutes;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public CurrentUserPrincipal parseToken(String token) {
        Claims claims = parseClaims(token);
        String userId = claims.getSubject();
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);
        return new CurrentUserPrincipal(UUID.fromString(userId), email, role);
    }

    public int getAccessTokenExpirySeconds() {
        return accessMinutes * 60;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException ex) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Invalid or expired token.");
        }
    }
}
