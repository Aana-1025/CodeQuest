package com.codequest.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.codequest.auth.dto.LoginRequest;
import com.codequest.auth.dto.LoginResponse;
import com.codequest.auth.dto.RegisterRequest;
import com.codequest.course.Course;
import com.codequest.course.CourseDifficulty;
import com.codequest.course.CourseRepository;
import com.codequest.course.CourseSourceType;
import com.codequest.progress.ProgressRepository;
import com.codequest.user.User;
import com.codequest.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LevelControllerTest {

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

    @Autowired
    private ProgressRepository progressRepository;

    @Test
    void shouldCompleteFirstLevelForAuthenticatedUserAndAwardXp() throws Exception {
        LoginResponse loginResponse = registerAndLogin("level-complete-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Level level = createLevelForUser(user, 1, false, 50);
        int startingXp = user.getXp();

        mockMvc.perform(post("/api/levels/{levelId}/complete", level.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.levelId").value(level.getId().toString()))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.alreadyCompleted").value(false))
                .andExpect(jsonPath("$.xpAwarded").value(level.getXpReward()))
                .andExpect(jsonPath("$.totalXp").value(startingXp + level.getXpReward()))
                .andExpect(jsonPath("$.completedAt").exists())
                .andExpect(jsonPath("$.userId").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist());

        assertEquals(1L, progressRepository.countByUserIdAndLevelId(user.getId(), level.getId()));
        assertEquals(startingXp + level.getXpReward(), userRepository.findById(user.getId()).orElseThrow().getXp());
    }

    @Test
    void shouldReturnIdempotentResponseForRepeatedCompletionWithoutAwardingXpAgain() throws Exception {
        LoginResponse loginResponse = registerAndLogin("level-repeat-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Level level = createLevelForUser(user, 1, false, 50);

        mockMvc.perform(post("/api/levels/{levelId}/complete", level.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk());

        int xpAfterFirstCompletion = userRepository.findById(user.getId()).orElseThrow().getXp();

        mockMvc.perform(post("/api/levels/{levelId}/complete", level.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.alreadyCompleted").value(true))
                .andExpect(jsonPath("$.xpAwarded").value(0))
                .andExpect(jsonPath("$.totalXp").value(xpAfterFirstCompletion))
                .andExpect(jsonPath("$.completedAt").exists());

        assertEquals(1L, progressRepository.countByUserIdAndLevelId(user.getId(), level.getId()));
        assertEquals(xpAfterFirstCompletion, userRepository.findById(user.getId()).orElseThrow().getXp());
    }

    @Test
    void shouldAllowDifferentUsersToCompleteSameLevelSeparately() throws Exception {
        LoginResponse firstLoginResponse = registerAndLogin("level-user-one-" + System.currentTimeMillis() + "@example.com");
        LoginResponse secondLoginResponse = registerAndLogin("level-user-two-" + System.currentTimeMillis() + "@example.com");
        User firstUser = userRepository.findByEmail(firstLoginResponse.email()).orElseThrow();
        User secondUser = userRepository.findByEmail(secondLoginResponse.email()).orElseThrow();
        Course course = createCourseForUser(firstUser);
        Level firstLevel = createLevel(course, 1, false, 50, "First Level");
        Level secondLevel = createLevel(course, 2, false, 75, "Second Level");

        mockMvc.perform(post("/api/levels/{levelId}/complete", firstLevel.getId())
                        .header("Authorization", "Bearer " + firstLoginResponse.accessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/levels/{levelId}/complete", secondLevel.getId())
                        .header("Authorization", "Bearer " + firstLoginResponse.accessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/levels/{levelId}/complete", secondLevel.getId())
                        .header("Authorization", "Bearer " + secondLoginResponse.accessToken()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));

        assertEquals(1L, progressRepository.countByUserIdAndLevelId(firstUser.getId(), firstLevel.getId()));
        assertEquals(1L, progressRepository.countByUserIdAndLevelId(firstUser.getId(), secondLevel.getId()));
        assertEquals(0L, progressRepository.countByUserIdAndLevelId(secondUser.getId(), secondLevel.getId()));
        assertEquals(155, userRepository.findById(firstUser.getId()).orElseThrow().getXp());
        assertEquals(30, userRepository.findById(secondUser.getId()).orElseThrow().getXp());
    }

    @Test
    void shouldReturn403WhenSecondLevelIsLocked() throws Exception {
        LoginResponse loginResponse = registerAndLogin("level-locked-second-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Course course = createCourseForUser(user);
        createLevel(course, 1, false, 50, "First Level");
        Level secondLevel = createLevel(course, 2, false, 75, "Second Level");
        int startingXp = user.getXp();

        mockMvc.perform(post("/api/levels/{levelId}/complete", secondLevel.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Complete previous levels before unlocking this level."))
                .andExpect(jsonPath("$.path").value("/api/levels/" + secondLevel.getId() + "/complete"));

        assertEquals(0L, progressRepository.countByUserIdAndLevelId(user.getId(), secondLevel.getId()));
        assertEquals(startingXp, userRepository.findById(user.getId()).orElseThrow().getXp());
    }

    @Test
    void shouldReturn403WhenBossLevelIsLocked() throws Exception {
        LoginResponse loginResponse = registerAndLogin("level-locked-boss-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Course course = createCourseForUser(user);
        createLevel(course, 1, false, 50, "First Level");
        createLevel(course, 2, false, 75, "Second Level");
        Level bossLevel = createLevel(course, 3, true, 100, "Boss Level");
        int startingXp = user.getXp();

        mockMvc.perform(post("/api/levels/{levelId}/complete", bossLevel.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Complete previous levels before unlocking this level."));

        assertEquals(0L, progressRepository.countByUserIdAndLevelId(user.getId(), bossLevel.getId()));
        assertEquals(startingXp, userRepository.findById(user.getId()).orElseThrow().getXp());
    }

    @Test
    void shouldAllowSecondLevelAfterFirstLevelIsCompleted() throws Exception {
        LoginResponse loginResponse = registerAndLogin("level-unlocked-second-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Course course = createCourseForUser(user);
        Level firstLevel = createLevel(course, 1, false, 50, "First Level");
        Level secondLevel = createLevel(course, 2, false, 75, "Second Level");

        mockMvc.perform(post("/api/levels/{levelId}/complete", firstLevel.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/levels/{levelId}/complete", secondLevel.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alreadyCompleted").value(false))
                .andExpect(jsonPath("$.xpAwarded").value(75));
    }

    @Test
    void shouldAllowBossLevelAfterAllPreviousLevelsAreCompleted() throws Exception {
        LoginResponse loginResponse = registerAndLogin("level-unlocked-boss-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Course course = createCourseForUser(user);
        Level firstLevel = createLevel(course, 1, false, 50, "First Level");
        Level secondLevel = createLevel(course, 2, false, 75, "Second Level");
        Level bossLevel = createLevel(course, 3, true, 100, "Boss Level");

        mockMvc.perform(post("/api/levels/{levelId}/complete", firstLevel.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/levels/{levelId}/complete", secondLevel.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/levels/{levelId}/complete", bossLevel.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alreadyCompleted").value(false))
                .andExpect(jsonPath("$.xpAwarded").value(100));
    }

    @Test
    void shouldReturn404WhenLevelMissing() throws Exception {
        LoginResponse loginResponse = registerAndLogin("level-missing-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        UUID missingLevelId = UUID.randomUUID();
        int startingXp = user.getXp();

        mockMvc.perform(post("/api/levels/{levelId}/complete", missingLevelId)
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Level not found."))
                .andExpect(jsonPath("$.path").value("/api/levels/" + missingLevelId + "/complete"));

        assertEquals(startingXp, userRepository.findById(user.getId()).orElseThrow().getXp());
    }

    @Test
    void shouldReturn401WhenCompletingLevelWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/levels/{levelId}/complete", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    private Course createCourseForUser(User user) {
        Instant now = Instant.now();
        Course course = new Course(
                UUID.randomUUID(),
                "level-completion-topic-" + UUID.randomUUID(),
                "Level Completion Course",
                "A seeded course for level completion tests.",
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

    private Level createLevelForUser(User user, int orderNumber, boolean isBoss, int xpReward) {
        return createLevel(createCourseForUser(user), orderNumber, isBoss, xpReward, "Level Completion Level");
    }

    private Level createLevel(Course course, int orderNumber, boolean isBoss, int xpReward, String title) {
        Instant now = Instant.now();
        Level level = new Level(
                UUID.randomUUID(),
                course,
                title,
                "# Level Completion Level",
                orderNumber,
                isBoss,
                xpReward,
                now,
                now
        );
        return levelRepository.save(level);
    }

    private LoginResponse registerAndLogin(String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("Level Test", email, "LevelPass123");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(email, "LevelPass123");
        String loginResponseStr = mockMvc.perform(post("/api/auth/login")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(loginResponseStr, LoginResponse.class);
    }
}
