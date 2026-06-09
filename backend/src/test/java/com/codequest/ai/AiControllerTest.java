package com.codequest.ai;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.codequest.ai.dto.ReviewCodeResponse;
import com.codequest.auth.dto.LoginRequest;
import com.codequest.auth.dto.LoginResponse;
import com.codequest.auth.dto.RegisterRequest;
import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AiCodeReviewService aiCodeReviewService;

    @Test
    void shouldReturnSafeCodeReviewForAuthenticatedUser() throws Exception {
        LoginResponse loginResponse = registerAndLogin("aireview-" + System.currentTimeMillis() + "@example.com");
        when(aiCodeReviewService.reviewCode(any())).thenReturn(new ReviewCodeResponse(
                "O(log n)",
                "O(1)",
                java.util.List.of(),
                java.util.List.of("Use left + (right - left) / 2."),
                "Binary search is already optimal for sorted input.",
                "Good job using a logarithmic strategy."
        ));

        mockMvc.perform(post("/api/ai/review-code")
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "language": "java",
                                  "problemTitle": "Binary Search",
                                  "problemDescription": "Find the target index.",
                                  "code": "public class Main {}"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timeComplexity").value("O(log n)"))
                .andExpect(jsonPath("$.spaceComplexity").value("O(1)"))
                .andExpect(jsonPath("$.correctnessIssues").isArray())
                .andExpect(jsonPath("$.improvements").isArray())
                .andExpect(jsonPath("$.betterApproach").value("Binary search is already optimal for sorted input."))
                .andExpect(jsonPath("$.encouragement").value("Good job using a logarithmic strategy."))
                .andExpect(jsonPath("$.userId").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.password_hash").doesNotExist())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(jsonPath("$.tokenHash").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist())
                .andExpect(jsonPath("$.secret").doesNotExist())
                .andExpect(jsonPath("$.correctAnswer").doesNotExist())
                .andExpect(jsonPath("$.hidden").doesNotExist())
                .andExpect(jsonPath("$.stdin").doesNotExist())
                .andExpect(jsonPath("$.expectedOutput").doesNotExist())
                .andExpect(jsonPath("$.rawPrompt").doesNotExist())
                .andExpect(jsonPath("$.rawGemini").doesNotExist())
                .andExpect(jsonPath("$.stackTrace").doesNotExist())
                .andExpect(content().string(not(containsString("java.lang"))))
                .andExpect(content().string(not(containsString("org.springframework"))));
    }

    @Test
    void shouldReturn401WithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/ai/review-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "language": "java",
                                  "code": "public class Main {}"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400ForInvalidLanguage() throws Exception {
        LoginResponse loginResponse = registerAndLogin("aireviewbadlang-" + System.currentTimeMillis() + "@example.com");
        when(aiCodeReviewService.reviewCode(any())).thenThrow(
                new ApiException(ErrorCode.BAD_REQUEST, "Language must be one of: java, python, javascript, cpp.")
        );

        mockMvc.perform(post("/api/ai/review-code")
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "language": "ruby",
                                  "code": "puts 'hello'"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Language must be one of: java, python, javascript, cpp."));
    }

    @Test
    void shouldReturn400AndNotCallServiceWhenCodeIsBlank() throws Exception {
        LoginResponse loginResponse = registerAndLogin("aireviewblank-" + System.currentTimeMillis() + "@example.com");

        mockMvc.perform(post("/api/ai/review-code")
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "language": "java",
                                  "code": " "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(aiCodeReviewService);
    }

    @Test
    void shouldReturn400AndNotCallServiceWhenCodeIsTooLong() throws Exception {
        LoginResponse loginResponse = registerAndLogin("aireviewlong-" + System.currentTimeMillis() + "@example.com");
        String requestBody = objectMapper.writeValueAsString(java.util.Map.of(
                "language", "java",
                "code", "a".repeat(20001)
        ));

        mockMvc.perform(post("/api/ai/review-code")
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(aiCodeReviewService);
    }

    @Test
    void shouldReturn400AndNotCallServiceWhenLanguageIsMissing() throws Exception {
        LoginResponse loginResponse = registerAndLogin("aireviewnolanguage-" + System.currentTimeMillis() + "@example.com");

        mockMvc.perform(post("/api/ai/review-code")
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "public class Main {}"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(aiCodeReviewService);
    }

    @Test
    void shouldReturn400AndNotCallServiceWhenCodeIsMissing() throws Exception {
        LoginResponse loginResponse = registerAndLogin("aireviewnocode-" + System.currentTimeMillis() + "@example.com");

        mockMvc.perform(post("/api/ai/review-code")
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "language": "java"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(aiCodeReviewService);
    }

    private LoginResponse registerAndLogin(String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("AI Review Test", email, "StrongPass123");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(email, "StrongPass123");
        String loginResponseStr = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(loginResponseStr, LoginResponse.class);
    }
}
