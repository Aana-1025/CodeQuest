package com.codequest.progress;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

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
import com.codequest.course.Course;
import com.codequest.course.CourseDifficulty;
import com.codequest.course.CourseRepository;
import com.codequest.course.CourseSourceType;
import com.codequest.level.Level;
import com.codequest.level.LevelRepository;
import com.codequest.user.User;
import com.codequest.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Test
    void shouldReturnCourseProgressForAuthenticatedUserWithSafeOrderedLevels() throws Exception {
        LoginResponse loginResponse = registerAndLogin("progress-fetch-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Course course = createCourseForUser(user);
        Level firstLevel = createLevel(course, 1, false, 50, "First Level");
        Level secondLevel = createLevel(course, 2, false, 75, "Second Level");
        Level bossLevel = createLevel(course, 3, true, 100, "Boss Level");

        mockMvc.perform(post("/api/levels/{levelId}/complete", firstLevel.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/progress/courses/{courseId}", course.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value(course.getId().toString()))
                .andExpect(jsonPath("$.completedLevels").value(1))
                .andExpect(jsonPath("$.totalLevels").value(3))
                .andExpect(jsonPath("$.progressPercent").value(33))
                .andExpect(jsonPath("$.courseCompleted").value(false))
                .andExpect(jsonPath("$.levels[0].levelId").value(firstLevel.getId().toString()))
                .andExpect(jsonPath("$.levels[0].orderNumber").value(1))
                .andExpect(jsonPath("$.levels[0].title").value("First Level"))
                .andExpect(jsonPath("$.levels[0].isBoss").value(false))
                .andExpect(jsonPath("$.levels[0].xpReward").value(50))
                .andExpect(jsonPath("$.levels[0].completed").value(true))
                .andExpect(jsonPath("$.levels[0].unlocked").value(true))
                .andExpect(jsonPath("$.levels[0].completedAt").exists())
                .andExpect(jsonPath("$.levels[1].levelId").value(secondLevel.getId().toString()))
                .andExpect(jsonPath("$.levels[1].orderNumber").value(2))
                .andExpect(jsonPath("$.levels[1].completed").value(false))
                .andExpect(jsonPath("$.levels[1].unlocked").value(true))
                .andExpect(jsonPath("$.levels[1].completedAt").isEmpty())
                .andExpect(jsonPath("$.levels[2].levelId").value(bossLevel.getId().toString()))
                .andExpect(jsonPath("$.levels[2].orderNumber").value(3))
                .andExpect(jsonPath("$.levels[2].isBoss").value(true))
                .andExpect(jsonPath("$.levels[2].completed").value(false))
                .andExpect(jsonPath("$.levels[2].unlocked").value(false))
                .andExpect(jsonPath("$.levels[2].completedAt").isEmpty())
                .andExpect(jsonPath("$.userId").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(jsonPath("$.levels[0].correctAnswer").doesNotExist())
                .andExpect(jsonPath("$.levels[0].contentMarkdown").doesNotExist());
    }

    @Test
    void shouldKeepCourseProgressUserScoped() throws Exception {
        LoginResponse firstLoginResponse = registerAndLogin("progress-first-" + System.currentTimeMillis() + "@example.com");
        LoginResponse secondLoginResponse = registerAndLogin("progress-second-" + System.currentTimeMillis() + "@example.com");
        User firstUser = userRepository.findByEmail(firstLoginResponse.email()).orElseThrow();
        User secondUser = userRepository.findByEmail(secondLoginResponse.email()).orElseThrow();
        Course course = createCourseForUser(firstUser);
        Level firstLevel = createLevel(course, 1, false, 50, "First Level");
        createLevel(course, 2, false, 75, "Second Level");
        createLevel(course, 3, true, 100, "Boss Level");

        mockMvc.perform(post("/api/levels/{levelId}/complete", firstLevel.getId())
                        .header("Authorization", "Bearer " + firstLoginResponse.accessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/progress/courses/{courseId}", course.getId())
                        .header("Authorization", "Bearer " + secondLoginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completedLevels").value(0))
                .andExpect(jsonPath("$.totalLevels").value(3))
                .andExpect(jsonPath("$.progressPercent").value(0))
                .andExpect(jsonPath("$.courseCompleted").value(false))
                .andExpect(jsonPath("$.levels[0].completed").value(false))
                .andExpect(jsonPath("$.levels[0].unlocked").value(true))
                .andExpect(jsonPath("$.levels[1].completed").value(false))
                .andExpect(jsonPath("$.levels[1].unlocked").value(false))
                .andExpect(jsonPath("$.levels[2].completed").value(false))
                .andExpect(jsonPath("$.levels[2].unlocked").value(false))
                .andExpect(jsonPath("$.levels[0].completedAt").isEmpty())
                .andExpect(jsonPath("$.levels[1].completedAt").isEmpty())
                .andExpect(jsonPath("$.levels[2].completedAt").isEmpty());
    }

    @Test
    void shouldReturn404WhenCourseMissing() throws Exception {
        LoginResponse loginResponse = registerAndLogin("progress-missing-" + System.currentTimeMillis() + "@example.com");
        UUID missingCourseId = UUID.randomUUID();

        mockMvc.perform(get("/api/progress/courses/{courseId}", missingCourseId)
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Course not found."))
                .andExpect(jsonPath("$.path").value("/api/progress/courses/" + missingCourseId));
    }

    @Test
    void shouldReturn401WhenGettingCourseProgressWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/progress/courses/{courseId}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    private Course createCourseForUser(User user) {
        Instant now = Instant.now();
        Course course = new Course(
                UUID.randomUUID(),
                "progress-fetch-topic-" + UUID.randomUUID(),
                "Progress Fetch Course",
                "A seeded course for progress fetch tests.",
                user,
                CourseDifficulty.BEGINNER,
                false,
                225,
                CourseSourceType.AI,
                now,
                now
        );
        return courseRepository.save(course);
    }

    private Level createLevel(Course course, int orderNumber, boolean isBoss, int xpReward, String title) {
        Instant now = Instant.now();
        Level level = new Level(
                UUID.randomUUID(),
                course,
                title,
                "# Progress Fetch Level",
                orderNumber,
                isBoss,
                xpReward,
                now,
                now
        );
        return levelRepository.save(level);
    }

    private LoginResponse registerAndLogin(String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("Progress Test", email, "ProgressPass123");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(email, "ProgressPass123");
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
