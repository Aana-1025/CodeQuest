package com.codequest.problem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Map;
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
import com.codequest.problem.dto.CodeSubmissionHistoryItemResponse;
import com.codequest.problem.dto.CodeSubmissionHistoryResponse;
import com.codequest.problem.dto.RunCodeResponse;
import com.codequest.problem.dto.SubmitCodeResponse;
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

    @Test
    void shouldReturnSafeSubmitCodeResponseForAuthenticatedUser() throws Exception {
        LoginResponse loginResponse = registerAndLogin("problemsubmit-" + System.currentTimeMillis() + "@example.com");
        UUID problemId = UUID.randomUUID();
        when(problemService.submitCode(eq(loginResponse.userId()), eq(problemId), any())).thenReturn(new SubmitCodeResponse(
                problemId,
                "java",
                "Hello CodeQuest\n",
                "",
                "Hello CodeQuest\n",
                0,
                null,
                true,
                100,
                true,
                "Solution accepted. XP awarded."
        ));

        mockMvc.perform(post("/api/problems/{problemId}/submit", problemId)
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
                .andExpect(jsonPath("$.xpAwarded").value(100))
                .andExpect(jsonPath("$.firstAccepted").value(true))
                .andExpect(jsonPath("$.message").value("Solution accepted. XP awarded."))
                .andExpect(jsonPath("$.userId").doesNotExist())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.correctAnswer").doesNotExist())
                .andExpect(jsonPath("$.hidden").doesNotExist())
                .andExpect(jsonPath("$.compile").doesNotExist())
                .andExpect(jsonPath("$.run").doesNotExist())
                .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }

    @Test
    void shouldReturn401WhenSubmittingCodeWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/problems/{problemId}/submit", UUID.randomUUID())
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
    void shouldReturn400AndNotCallServiceWhenSubmitLanguageIsBlank() throws Exception {
        LoginResponse loginResponse = registerAndLogin("problemsubmitbadlang-" + System.currentTimeMillis() + "@example.com");

        mockMvc.perform(post("/api/problems/{problemId}/submit", UUID.randomUUID())
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "language": "",
                                  "code": "public class Main {}",
                                  "stdin": "",
                                  "expectedOutput": "Hello"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(problemService);
    }

    @Test
    void shouldReturn400AndNotCallServiceWhenSubmitCodeIsBlank() throws Exception {
        LoginResponse loginResponse = registerAndLogin("problemsubmitblank-" + System.currentTimeMillis() + "@example.com");

        mockMvc.perform(post("/api/problems/{problemId}/submit", UUID.randomUUID())
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "language": "java",
                                  "code": " ",
                                  "stdin": "",
                                  "expectedOutput": "Hello"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(problemService);
    }

    @Test
    void shouldReturn400AndNotCallServiceWhenSubmitCodeIsTooLong() throws Exception {
        LoginResponse loginResponse = registerAndLogin("problemsubmitlong-" + System.currentTimeMillis() + "@example.com");
        String longCode = "a".repeat(20001);
        String requestBody = objectMapper.writeValueAsString(Map.of(
                "language", "java",
                "code", longCode,
                "stdin", "",
                "expectedOutput", "Hello"
        ));

        mockMvc.perform(post("/api/problems/{problemId}/submit", UUID.randomUUID())
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verifyNoInteractions(problemService);
    }

    @Test
    void shouldReturn503WhenSubmitMapsRunnerUnavailable() throws Exception {
        LoginResponse loginResponse = registerAndLogin("problemsubmit503-" + System.currentTimeMillis() + "@example.com");
        UUID problemId = UUID.randomUUID();
        when(problemService.submitCode(eq(loginResponse.userId()), eq(problemId), any())).thenThrow(
                new com.codequest.common.exception.ApiException(
                        com.codequest.common.exception.ErrorCode.CODE_RUNNER_UNAVAILABLE,
                        "Code runner is currently unavailable. Please try again later."
                )
        );

        mockMvc.perform(post("/api/problems/{problemId}/submit", problemId)
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "language": "java",
                                  "code": "public class Main {}",
                                  "stdin": "",
                                  "expectedOutput": "Hello"
                                }
                                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("CODE_RUNNER_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value("Code runner is currently unavailable. Please try again later."))
                .andExpect(jsonPath("$.stackTrace").doesNotExist());
    }

    @Test
    void shouldReturnSubmissionHistoryForAuthenticatedUserWithDefaultPagination() throws Exception {
        LoginResponse loginResponse = registerAndLogin("problemhistorydefault-" + System.currentTimeMillis() + "@example.com");
        UUID problemId = UUID.randomUUID();
        when(problemService.getSubmissionHistory(eq(loginResponse.userId()), eq(problemId), eq(0), eq(20)))
                .thenReturn(historyResponse(problemId, 0, 20));

        mockMvc.perform(get("/api/problems/{problemId}/submissions", problemId)
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.problemId").value(problemId.toString()))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.items[0].problemId").value(problemId.toString()))
                .andExpect(jsonPath("$.items[0].language").value("java"))
                .andExpect(jsonPath("$.items[0].code").value("public class Main { public static void main(String[] args) { System.out.println(2); } }"))
                .andExpect(jsonPath("$.items[0].passed").value(true))
                .andExpect(jsonPath("$.items[0].passedTestCases").value(1))
                .andExpect(jsonPath("$.items[0].totalTestCases").value(1))
                .andExpect(jsonPath("$.items[0].userId").doesNotExist())
                .andExpect(jsonPath("$.items[0].password").doesNotExist())
                .andExpect(jsonPath("$.items[0].passwordHash").doesNotExist())
                .andExpect(jsonPath("$.items[0].password_hash").doesNotExist())
                .andExpect(jsonPath("$.items[0].token").doesNotExist())
                .andExpect(jsonPath("$.items[0].accessToken").doesNotExist())
                .andExpect(jsonPath("$.items[0].refreshToken").doesNotExist())
                .andExpect(jsonPath("$.items[0].tokenHash").doesNotExist())
                .andExpect(jsonPath("$.items[0].role").doesNotExist())
                .andExpect(jsonPath("$.items[0].secret").doesNotExist())
                .andExpect(jsonPath("$.items[0].correctAnswer").doesNotExist())
                .andExpect(jsonPath("$.items[0].hidden").doesNotExist())
                .andExpect(jsonPath("$.items[0].expectedOutput").doesNotExist())
                .andExpect(jsonPath("$.items[0].stdin").doesNotExist())
                .andExpect(jsonPath("$.items[0].compile").doesNotExist())
                .andExpect(jsonPath("$.items[0].run").doesNotExist())
                .andExpect(jsonPath("$.stackTrace").doesNotExist())
                .andExpect(content().string(not(containsString("java.lang"))))
                .andExpect(content().string(not(containsString("org.springframework"))));
    }

    @Test
    void shouldReturnSubmissionHistoryForAuthenticatedUserWithExplicitPagination() throws Exception {
        LoginResponse loginResponse = registerAndLogin("problemhistorypage-" + System.currentTimeMillis() + "@example.com");
        UUID problemId = UUID.randomUUID();
        when(problemService.getSubmissionHistory(eq(loginResponse.userId()), eq(problemId), eq(1), eq(1)))
                .thenReturn(historyResponse(problemId, 1, 1));

        mockMvc.perform(get("/api/problems/{problemId}/submissions", problemId)
                        .param("page", "1")
                        .param("size", "1")
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(1));
    }

    @Test
    void shouldReturn401WhenFetchingSubmissionHistoryWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/problems/{problemId}/submissions", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400WhenSubmissionHistoryPageIsInvalid() throws Exception {
        LoginResponse loginResponse = registerAndLogin("problemhistorybadpage-" + System.currentTimeMillis() + "@example.com");
        UUID problemId = UUID.randomUUID();
        when(problemService.getSubmissionHistory(eq(loginResponse.userId()), eq(problemId), eq(-1), eq(20))).thenThrow(
                new com.codequest.common.exception.ApiException(
                        com.codequest.common.exception.ErrorCode.BAD_REQUEST,
                        "Page must be greater than or equal to 0."
                )
        );

        mockMvc.perform(get("/api/problems/{problemId}/submissions", problemId)
                        .param("page", "-1")
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Page must be greater than or equal to 0."));
    }

    @Test
    void shouldReturn400WhenSubmissionHistorySizeIsInvalid() throws Exception {
        LoginResponse loginResponse = registerAndLogin("problemhistorybadsize-" + System.currentTimeMillis() + "@example.com");
        UUID problemId = UUID.randomUUID();
        when(problemService.getSubmissionHistory(eq(loginResponse.userId()), eq(problemId), eq(0), eq(51))).thenThrow(
                new com.codequest.common.exception.ApiException(
                        com.codequest.common.exception.ErrorCode.BAD_REQUEST,
                        "Size must be less than or equal to 50."
                )
        );

        mockMvc.perform(get("/api/problems/{problemId}/submissions", problemId)
                        .param("size", "51")
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Size must be less than or equal to 50."));
    }

    @Test
    void shouldReturn400WhenSubmissionHistorySizeIsZero() throws Exception {
        LoginResponse loginResponse = registerAndLogin("problemhistorysizezero-" + System.currentTimeMillis() + "@example.com");
        UUID problemId = UUID.randomUUID();
        when(problemService.getSubmissionHistory(eq(loginResponse.userId()), eq(problemId), eq(0), eq(0))).thenThrow(
                new com.codequest.common.exception.ApiException(
                        com.codequest.common.exception.ErrorCode.BAD_REQUEST,
                        "Size must be at least 1."
                )
        );

        mockMvc.perform(get("/api/problems/{problemId}/submissions", problemId)
                        .param("size", "0")
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Size must be at least 1."));
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

    private CodeSubmissionHistoryResponse historyResponse(UUID problemId, int page, int size) {
        return new CodeSubmissionHistoryResponse(
                problemId,
                page,
                size,
                1,
                1,
                List.of(new CodeSubmissionHistoryItemResponse(
                        UUID.randomUUID(),
                        problemId,
                        "java",
                        "public class Main { public static void main(String[] args) { System.out.println(2); } }",
                        true,
                        1,
                        1,
                        null,
                        null,
                        null,
                        Instant.parse("2026-06-09T10:01:00Z")
                ))
        );
    }
}
