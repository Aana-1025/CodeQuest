package com.codequest.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codequest.auth.dto.LoginRequest;
import com.codequest.auth.dto.RegisterRequest;
import com.codequest.auth.dto.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetCurrentUserProfileSuccessfully() throws Exception {
        String email = "profiletest-" + System.currentTimeMillis() + "@example.com";
        // Register a user
        RegisterRequest registerRequest = new RegisterRequest(
                "Profile Test",
                email,
                "ProfilePass123"
        );
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Login to get access token
        LoginRequest loginRequest = new LoginRequest(
                email,
                "ProfilePass123"
        );
        String loginResponseStr = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        LoginResponse loginResponse = objectMapper.readValue(loginResponseStr, LoginResponse.class);
        String accessToken = loginResponse.accessToken();

        // Call GET /api/users/me with token
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.name").value("Profile Test"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.rank").value("BEGINNER"))
                .andExpect(jsonPath("$.xp").value(0))
                .andExpect(jsonPath("$.streak").value(0))
                .andExpect(jsonPath("$.goal").isEmpty())
                .andExpect(jsonPath("$.avatarUrl").isEmpty())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.password_hash").doesNotExist())
                .andExpect(jsonPath("$.tokenHash").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist());
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }
}