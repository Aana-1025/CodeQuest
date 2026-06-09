package com.codequest.leaderboard;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
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
import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.leaderboard.dto.CurrentUserLeaderboardResponse;
import com.codequest.leaderboard.dto.LeaderboardItemResponse;
import com.codequest.leaderboard.dto.LeaderboardResponse;
import com.codequest.user.UserRank;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LeaderboardService leaderboardService;

    @Test
    void authenticatedGetReturns200() throws Exception {
        LoginResponse loginResponse = registerAndLogin("leaderboard-" + System.currentTimeMillis() + "@example.com");
        when(leaderboardService.getLeaderboard(eq(loginResponse.userId()), eq(0), eq(50), eq("ALL_TIME")))
                .thenReturn(sampleResponse(loginResponse.userId()));

        mockMvc.perform(get("/api/leaderboard")
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk());
    }

    @Test
    void noTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void defaultQueryParamsWork() throws Exception {
        LoginResponse loginResponse = registerAndLogin("leaderboarddefault-" + System.currentTimeMillis() + "@example.com");
        when(leaderboardService.getLeaderboard(eq(loginResponse.userId()), eq(0), eq(50), eq("ALL_TIME")))
                .thenReturn(sampleResponse(loginResponse.userId()));

        mockMvc.perform(get("/api/leaderboard")
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(50))
                .andExpect(jsonPath("$.period").value("ALL_TIME"));
    }

    @Test
    void explicitQueryParamsWork() throws Exception {
        LoginResponse loginResponse = registerAndLogin("leaderboardexplicit-" + System.currentTimeMillis() + "@example.com");
        when(leaderboardService.getLeaderboard(eq(loginResponse.userId()), eq(0), eq(50), eq("ALL_TIME")))
                .thenReturn(sampleResponse(loginResponse.userId()));

        mockMvc.perform(get("/api/leaderboard")
                        .param("page", "0")
                        .param("size", "50")
                        .param("period", "ALL_TIME")
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(50))
                .andExpect(jsonPath("$.period").value("ALL_TIME"));
    }

    @Test
    void invalidPageReturns400() throws Exception {
        LoginResponse loginResponse = registerAndLogin("leaderboardbadpage-" + System.currentTimeMillis() + "@example.com");
        when(leaderboardService.getLeaderboard(eq(loginResponse.userId()), eq(-1), eq(50), eq("ALL_TIME")))
                .thenThrow(new ApiException(ErrorCode.BAD_REQUEST, "Page must be greater than or equal to 0."));

        mockMvc.perform(get("/api/leaderboard")
                        .param("page", "-1")
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Page must be greater than or equal to 0."));
    }

    @Test
    void invalidSizeZeroReturns400() throws Exception {
        LoginResponse loginResponse = registerAndLogin("leaderboardsizezero-" + System.currentTimeMillis() + "@example.com");
        when(leaderboardService.getLeaderboard(eq(loginResponse.userId()), eq(0), eq(0), eq("ALL_TIME")))
                .thenThrow(new ApiException(ErrorCode.BAD_REQUEST, "Size must be at least 1."));

        mockMvc.perform(get("/api/leaderboard")
                        .param("size", "0")
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Size must be at least 1."));
    }

    @Test
    void invalidSizeFiftyOneReturns400() throws Exception {
        LoginResponse loginResponse = registerAndLogin("leaderboardsizefiftyone-" + System.currentTimeMillis() + "@example.com");
        when(leaderboardService.getLeaderboard(eq(loginResponse.userId()), eq(0), eq(51), eq("ALL_TIME")))
                .thenThrow(new ApiException(ErrorCode.BAD_REQUEST, "Size must be less than or equal to 50."));

        mockMvc.perform(get("/api/leaderboard")
                        .param("size", "51")
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Size must be less than or equal to 50."));
    }

    @Test
    void invalidPeriodReturns400() throws Exception {
        LoginResponse loginResponse = registerAndLogin("leaderboardperiod-" + System.currentTimeMillis() + "@example.com");
        when(leaderboardService.getLeaderboard(eq(loginResponse.userId()), eq(0), eq(50), eq("WEEKLY")))
                .thenThrow(new ApiException(ErrorCode.BAD_REQUEST, "Period must be ALL_TIME."));

        mockMvc.perform(get("/api/leaderboard")
                        .param("period", "WEEKLY")
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Period must be ALL_TIME."));
    }

    @Test
    void jsonResponseHasExpectedShapeAndSafeFieldsOnly() throws Exception {
        LoginResponse loginResponse = registerAndLogin("leaderboardshape-" + System.currentTimeMillis() + "@example.com");
        when(leaderboardService.getLeaderboard(eq(loginResponse.userId()), eq(0), eq(50), eq("ALL_TIME")))
                .thenReturn(sampleResponse(loginResponse.userId()));

        mockMvc.perform(get("/api/leaderboard")
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(50))
                .andExpect(jsonPath("$.period").value("ALL_TIME"))
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.items[0].rankPosition").value(1))
                .andExpect(jsonPath("$.items[0].userId").value(loginResponse.userId().toString()))
                .andExpect(jsonPath("$.items[0].name").value("Leaderboard User"))
                .andExpect(jsonPath("$.items[0].xp").value(30))
                .andExpect(jsonPath("$.items[0].rank").value("BEGINNER"))
                .andExpect(jsonPath("$.items[0].streak").value(1))
                .andExpect(jsonPath("$.currentUser.rankPosition").value(1))
                .andExpect(jsonPath("$.currentUser.userId").value(loginResponse.userId().toString()))
                .andExpect(jsonPath("$.currentUser.xp").value(30))
                .andExpect(jsonPath("$.currentUser.rank").value("BEGINNER"))
                .andExpect(jsonPath("$.items[0].email").doesNotExist())
                .andExpect(jsonPath("$.items[0].password").doesNotExist())
                .andExpect(jsonPath("$.items[0].passwordHash").doesNotExist())
                .andExpect(jsonPath("$.items[0].password_hash").doesNotExist())
                .andExpect(jsonPath("$.items[0].token").doesNotExist())
                .andExpect(jsonPath("$.items[0].accessToken").doesNotExist())
                .andExpect(jsonPath("$.items[0].refreshToken").doesNotExist())
                .andExpect(jsonPath("$.items[0].tokenHash").doesNotExist())
                .andExpect(jsonPath("$.items[0].role").doesNotExist())
                .andExpect(jsonPath("$.items[0].secret").doesNotExist())
                .andExpect(jsonPath("$.items[0].lastLogin").doesNotExist())
                .andExpect(content().string(not(containsString("org.springframework"))))
                .andExpect(content().string(not(containsString("java.lang"))));
    }

    private LoginResponse registerAndLogin(String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("Leaderboard User", email, "StrongPass123");
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

    private LeaderboardResponse sampleResponse(UUID userId) {
        return new LeaderboardResponse(
                0,
                50,
                "ALL_TIME",
                1,
                1,
                List.of(new LeaderboardItemResponse(
                        1,
                        userId,
                        "Leaderboard User",
                        30,
                        UserRank.BEGINNER,
                        1
                )),
                new CurrentUserLeaderboardResponse(
                        1,
                        userId,
                        30,
                        UserRank.BEGINNER
                )
        );
    }
}
