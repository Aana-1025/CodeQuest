package com.codequest.auth;

import com.codequest.auth.dto.RegisterRequest;
import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.user.User;
import com.codequest.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import({AuthService.class, com.codequest.common.config.PasswordEncoderConfig.class})
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
}