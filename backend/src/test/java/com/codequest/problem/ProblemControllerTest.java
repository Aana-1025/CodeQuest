package com.codequest.problem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.codequest.auth.dto.LoginRequest;
import com.codequest.auth.dto.LoginResponse;
import com.codequest.auth.dto.RegisterRequest;
import com.codequest.problem.dto.RunCodeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProblemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProblemService problemService;

    @Test
    void shouldReturnSafeRunCodeResponseForAuthenticatedUser() throws Exception {
        LoginResponse loginResponse = registerAndLogin("problemrun-" + System.currentTimeMillis() + "@example.com");
        UUID problemId = UUID.randomUUID();
        when(problemService.runCode(eq(problemId), any())).thenReturn(new RunCodeResponse(
                problemId,
                "java",
                "Hello CodeQuest\n",
                "",
                "Hello CodeQuest\n",
                0,
                null,
                true,
                "Code executed successfully."
        ));

        mockMvc.perform(post("/api/problems/{problemId}/run", problemId)
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "language": "java",
                                  "code": "public class Main {}",
                                  "stdin": "",
                                  "expectedOutput": "Hello CodeQuest"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.problemId").value(problemId.toString()))
                .andExpect(jsonPath("$.language").value("java"))
                .andExpect(jsonPath("$.stdout").value("Hello CodeQuest\n"))
                .andExpect(jsonPath("$.stderr").value(""))
                .andExpect(jsonPath("$.output").value("Hello CodeQuest\n"))
                .andExpect(jsonPath("$.exitCode").value(0))
                .andExpect(jsonPath("$.passed").value(true))
                .andExpect(jsonPath("$.message").value("Code executed successfully."))
                .andExpect(jsonPath("$.userId").doesNotExist())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.correctAnswer").doesNotExist())
                .andExpect(jsonPath("$.hidden").doesNotExist())
                .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }

    @Test
    void shouldReturn401WhenRunningCodeWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/problems/{problemId}/run", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "language": "java",
                                  "code": "public class Main {}",
                                  "stdin": "",
                                  "expectedOutput": "Hello"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400WhenLanguageIsInvalid() throws Exception {
        LoginResponse loginResponse = registerAndLogin("problembadlang-" + System.currentTimeMillis() + "@example.com");
        UUID problemId = UUID.randomUUID();
        when(problemService.runCode(eq(problemId), any())).thenThrow(
                new com.codequest.common.exception.ApiException(
                        com.codequest.common.exception.ErrorCode.BAD_REQUEST,
                        "Language must be one of: java, python, javascript, cpp."
                )
        );

        mockMvc.perform(post("/api/problems/{problemId}/run", problemId)
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "language": "ruby",
                                  "code": "puts 'hi'",
                                  "stdin": "",
                                  "expectedOutput": "hi"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Language must be one of: java, python, javascript, cpp."));
    }

    @Test
    void shouldReturn503WhenProblemServiceMapsRunnerUnavailable() throws Exception {
        LoginResponse loginResponse = registerAndLogin("problem503-" + System.currentTimeMillis() + "@example.com");
        UUID problemId = UUID.randomUUID();
        when(problemService.runCode(eq(problemId), any())).thenThrow(
                new com.codequest.common.exception.ApiException(
                        com.codequest.common.exception.ErrorCode.CODE_RUNNER_UNAVAILABLE,
                        "Code runner is currently unavailable. Please try again later."
                )
        );

        mockMvc.perform(post("/api/problems/{problemId}/run", problemId)
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "language": "java",
                                  "code": "public class Main {}",
                                  "stdin": "",
                                  "expectedOutput": ""
                                }
                                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("CODE_RUNNER_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value("Code runner is currently unavailable. Please try again later."))
                .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }

    private LoginResponse registerAndLogin(String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("Problem Test", email, "ProblemPass123");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(email, "ProblemPass123");
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
