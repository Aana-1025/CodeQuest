package com.codequest.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.codequest.auth.JwtService;
import com.codequest.auth.RefreshTokenService;
import com.codequest.auth.dto.LoginRequest;
import com.codequest.auth.dto.LoginResponse;
import com.codequest.auth.dto.LogoutRequest;
import com.codequest.auth.dto.LogoutResponse;
import com.codequest.auth.dto.RefreshTokenRequest;
import com.codequest.auth.dto.RefreshTokenResponse;
import com.codequest.auth.dto.RegisterRequest;
import com.codequest.auth.mapper.AuthMapper;
import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.progress.StreakService;
import com.codequest.progress.XPService;
import com.codequest.user.User;
import com.codequest.user.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
@Import({
        AuthService.class,
        JwtService.class,
        RefreshTokenService.class,
        AuthMapper.class,
        XPService.class,
        StreakService.class,
        com.codequest.common.config.PasswordEncoderConfig.class
})
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                "john@example.com",
                "SecurePass123"
        );

        User user = authService.register(request);

        assertNotNull(user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertTrue(passwordEncoder.matches("SecurePass123", user.getPasswordHash()));
        assertEquals(0, user.getXp());
        assertEquals("BEGINNER", user.getRank().toString());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void shouldNormalizeEmailToLowercase() {
        RegisterRequest request = new RegisterRequest(
                "Jane Doe",
                "JANE@EXAMPLE.COM",
                "SecurePass123"
        );

        User user = authService.register(request);

        assertEquals("jane@example.com", user.getEmail());
    }

    @Test
    void shouldThrowExceptionForDuplicateEmail() {
        RegisterRequest firstRequest = new RegisterRequest(
                "User One",
                "user@example.com",
                "SecurePass123"
        );
        authService.register(firstRequest);

        RegisterRequest secondRequest = new RegisterRequest(
                "User Two",
                "user@example.com",
                "AnotherPass456"
        );

        ApiException exception = assertThrows(ApiException.class, () ->
                authService.register(secondRequest)
        );

        assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
        assertEquals("Email already registered.", exception.getMessage());
    }

    @Test
    void shouldHashPasswordBeforeSaving() {
        RegisterRequest request = new RegisterRequest(
                "Hash Test",
                "hash@example.com",
                "MyPassword123"
        );

        User user = authService.register(request);

        assertNotEquals("MyPassword123", user.getPasswordHash());
        assertTrue(passwordEncoder.matches("MyPassword123", user.getPasswordHash()));
    }

    @Test
    void shouldTrimNameAndEmail() {
        RegisterRequest request = new RegisterRequest(
                "  Trimmed Name  ",
                "  trimmed@example.com  ",
                "SecurePass123"
        );

        User user = authService.register(request);

        assertEquals("Trimmed Name", user.getName());
        assertEquals("trimmed@example.com", user.getEmail());
    }

    @Test
    void shouldLoginUserSuccessfully() {
        RegisterRequest registerRequest = new RegisterRequest(
                "Login Test",
                "login@example.com",
                "MyPassword123"
        );
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(
                "login@example.com",
                "MyPassword123"
        );

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response.userId());
        assertEquals("login@example.com", response.email());
        assertEquals("Login Test", response.name());
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertNotEquals(response.accessToken(), response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(900, response.expiresInSeconds());
        assertEquals(30, response.xp());
        assertEquals(1, response.streak());
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        RegisterRequest registerRequest = new RegisterRequest(
                "Refresh Test",
                "refresh@example.com",
                "RefreshPass123"
        );
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(
                "refresh@example.com",
                "RefreshPass123"
        );
        LoginResponse loginResponse = authService.login(loginRequest);

        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(loginResponse.refreshToken());
        RefreshTokenResponse refreshResponse = authService.refreshToken(refreshRequest);

        assertNotNull(refreshResponse.accessToken());
        assertNotNull(refreshResponse.refreshToken());
        assertNotEquals(loginResponse.accessToken(), refreshResponse.accessToken());
        assertNotEquals(loginResponse.refreshToken(), refreshResponse.refreshToken());
        assertEquals("Bearer", refreshResponse.tokenType());
        assertEquals(900, refreshResponse.expiresInSeconds());

        User user = userRepository.findByEmail("refresh@example.com").orElseThrow();
        assertEquals(30, user.getXp());
        assertEquals(1, user.getStreak());
    }

    @Test
    void shouldThrowExceptionForInvalidRefreshToken() {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("invalid-token");

        ApiException exception = assertThrows(ApiException.class, () ->
                authService.refreshToken(refreshRequest)
        );

        assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
        assertEquals("Invalid refresh token.", exception.getMessage());
    }

    @Test
    void shouldLoginWithNormalizedEmail() {
        RegisterRequest registerRequest = new RegisterRequest(
                "Email Test",
                "email@example.com",
                "TestPass123"
        );
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(
                "EMAIL@EXAMPLE.COM",
                "TestPass123"
        );

        LoginResponse response = authService.login(loginRequest);

        assertEquals("email@example.com", response.email());
    }

    @Test
    void shouldThrowExceptionForWrongPassword() {
        RegisterRequest registerRequest = new RegisterRequest(
                "Wrong Pass Test",
                "wrong@example.com",
                "CorrectPass123"
        );
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(
                "wrong@example.com",
                "WrongPass123"
        );

        ApiException exception = assertThrows(ApiException.class, () ->
                authService.login(loginRequest)
        );

        assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
        assertEquals("Invalid email or password.", exception.getMessage());

        User user = userRepository.findByEmail("wrong@example.com").orElseThrow();
        assertEquals(0, user.getXp());
        assertEquals(0, user.getStreak());
        assertEquals(null, user.getLastLogin());
    }

    @Test
    void shouldThrowExceptionForUnknownEmail() {
        LoginRequest loginRequest = new LoginRequest(
                "unknown@example.com",
                "SomePass123"
        );

        ApiException exception = assertThrows(ApiException.class, () ->
                authService.login(loginRequest)
        );

        assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
        assertEquals("Invalid email or password.", exception.getMessage());
    }

    @Test
    void shouldNotAwardDailyLoginXpTwiceOnSameDay() {
        RegisterRequest registerRequest = new RegisterRequest(
                "Daily Login Test",
                "daily@example.com",
                "DailyPass123"
        );
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(
                "daily@example.com",
                "DailyPass123"
        );

        LoginResponse firstLogin = authService.login(loginRequest);
        LoginResponse secondLogin = authService.login(loginRequest);

        assertEquals(30, firstLogin.xp());
        assertEquals(1, firstLogin.streak());
        assertEquals(30, secondLogin.xp());
        assertEquals(1, secondLogin.streak());

        User user = userRepository.findByEmail("daily@example.com").orElseThrow();
        assertEquals(30, user.getXp());
        assertEquals(1, user.getStreak());
        assertNotNull(user.getLastLogin());
    }

    @Test
    void shouldNotExposePasswordHashInLoginResponse() {
        RegisterRequest registerRequest = new RegisterRequest(
                "Privacy Test",
                "privacy@example.com",
                "PrivatePass123"
        );
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(
                "privacy@example.com",
                "PrivatePass123"
        );

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response.accessToken());
        assertEquals("Bearer", response.tokenType());
    }

    @Test
    void shouldLogoutUserSuccessfully() {
        RegisterRequest registerRequest = new RegisterRequest(
                "Logout Test",
                "logout@example.com",
                "LogoutPass123"
        );
        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(
                "logout@example.com",
                "LogoutPass123"
        );
        LoginResponse loginResponse = authService.login(loginRequest);

        LogoutRequest logoutRequest = new LogoutRequest(loginResponse.refreshToken());
        LogoutResponse logoutResponse = authService.logout(logoutRequest);

        assertEquals("Logged out successfully.", logoutResponse.message());
    }

    @Test
    void shouldThrowExceptionForInvalidRefreshTokenOnLogout() {
        LogoutRequest logoutRequest = new LogoutRequest("invalid-token");

        LogoutResponse response = authService.logout(logoutRequest);

        assertEquals("Logged out successfully.", response.message());
    }
}
