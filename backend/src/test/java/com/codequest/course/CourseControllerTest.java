package com.codequest.course;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.codequest.auth.dto.LoginRequest;
import com.codequest.auth.dto.LoginResponse;
import com.codequest.auth.dto.RegisterRequest;
import com.codequest.course.dto.GenerateCourseRequest;
import com.codequest.course.dto.GenerateCourseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGenerateCourseForAuthenticatedUser() throws Exception {
        String accessToken = registerAndLogin("coursetest-" + System.currentTimeMillis() + "@example.com");

        GenerateCourseRequest request = new GenerateCourseRequest("Binary Search", CourseDifficulty.BEGINNER, "DSA interview preparation");

        mockMvc.perform(post("/api/courses/generate")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").exists())
                .andExpect(jsonPath("$.title").value("Binary Search"))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.cacheHit").value(false))
                .andExpect(jsonPath("$.levels.length()").value(3))
                .andExpect(jsonPath("$.levels[0].levelId").exists())
                .andExpect(jsonPath("$.levels[0].title").exists())
                .andExpect(jsonPath("$.levels[0].orderNumber").value(1))
                .andExpect(jsonPath("$.levels[0].isBoss").value(false))
                .andExpect(jsonPath("$.levels[0].xpReward").value(50));
    }

    @Test
    void shouldReturn401WhenTokenMissing() throws Exception {
        GenerateCourseRequest request = new GenerateCourseRequest("Binary Search", CourseDifficulty.BEGINNER, null);

        mockMvc.perform(post("/api/courses/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400WhenTopicInvalid() throws Exception {
        String accessToken = registerAndLogin("invalidtopic-" + System.currentTimeMillis() + "@example.com");

        GenerateCourseRequest request = new GenerateCourseRequest(" ", CourseDifficulty.BEGINNER, null);

        mockMvc.perform(post("/api/courses/generate")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenDifficultyMissing() throws Exception {
        String accessToken = registerAndLogin("missingdifficulty-" + System.currentTimeMillis() + "@example.com");

        String requestBody = """
                {
                  "topic": "Binary Search",
                  "goal": "DSA"
                }
                """;

        mockMvc.perform(post("/api/courses/generate")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnCachedCourseOnSecondRequestWithNormalizedTopic() throws Exception {
        String accessToken = registerAndLogin("cachehit-" + System.currentTimeMillis() + "@example.com");

        GenerateCourseRequest firstRequest = new GenerateCourseRequest("Binary Search", CourseDifficulty.BEGINNER, "Goal");
        String firstResponseBody = mockMvc.perform(post("/api/courses/generate")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        GenerateCourseRequest secondRequest = new GenerateCourseRequest("  BINARY   SEARCH  ", CourseDifficulty.BEGINNER, "Goal");
        String secondResponseBody = mockMvc.perform(post("/api/courses/generate")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cacheHit").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        GenerateCourseResponse firstResponse = objectMapper.readValue(firstResponseBody, GenerateCourseResponse.class);
        GenerateCourseResponse secondResponse = objectMapper.readValue(secondResponseBody, GenerateCourseResponse.class);

        assertEquals(firstResponse.courseId(), secondResponse.courseId());
    }

    private String registerAndLogin(String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "Course Test",
                email,
                "CoursePass123"
        );
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(
                email,
                "CoursePass123"
        );
        String loginResponseStr = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        LoginResponse loginResponse = objectMapper.readValue(loginResponseStr, LoginResponse.class);
        return loginResponse.accessToken();
    }
}
