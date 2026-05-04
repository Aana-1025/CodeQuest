package com.codequest.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.common.security.CurrentUserPrincipal;
import com.codequest.user.User;
import com.codequest.user.UserRole;

class JwtServiceTest {

    private static final String SECRET = "test-jwt-secret-dev-only-change-this-secret";

    @Test
    void shouldGenerateAndValidateToken() {
        JwtService jwtService = new JwtService(SECRET, 15);
        User user = new User(UUID.randomUUID(), "Test User", "test@example.com", "hash");
        user.setRole(UserRole.STUDENT);

        String token = jwtService.generateAccessToken(user);

        assertNotNull(token);

        CurrentUserPrincipal principal = jwtService.parseToken(token);

        assertEquals(user.getId(), principal.userId());
        assertEquals("test@example.com", principal.email());
        assertEquals("STUDENT", principal.role());
    }

    @Test
    void shouldRejectInvalidToken() {
        JwtService jwtService = new JwtService(SECRET, 15);

        ApiException exception = assertThrows(ApiException.class, () -> jwtService.parseToken("invalid.token.value"));

        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    void shouldRejectExpiredToken() {
        JwtService jwtService = new JwtService(SECRET, -1);
        User user = new User(UUID.randomUUID(), "Expired User", "expired@example.com", "hash");
        user.setRole(UserRole.STUDENT);

        String token = jwtService.generateAccessToken(user);

        ApiException exception = assertThrows(ApiException.class, () -> jwtService.parseToken(token));

        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());
    }
}
