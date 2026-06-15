package com.codequest.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.codequest.auth.dto.LoginRequest;
import com.codequest.auth.dto.LoginResponse;
import com.codequest.auth.dto.RegisterRequest;
import com.codequest.course.Course;
import com.codequest.course.CourseDifficulty;
import com.codequest.course.CourseRepository;
import com.codequest.course.CourseSourceType;
import com.codequest.flashcard.Flashcard;
import com.codequest.flashcard.FlashcardRepository;
import com.codequest.problem.CodingProblem;
import com.codequest.problem.CodingProblemRepository;
import com.codequest.progress.ProgressRepository;
import com.codequest.quiz.Quiz;
import com.codequest.quiz.QuizRepository;
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

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private CodingProblemRepository codingProblemRepository;

    @Test
    void shouldReturnLevelDetailsForUnlockedLevelWithoutExposingHiddenFields() throws Exception {
        LoginResponse loginResponse = registerAndLogin("level-details-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Course course = createCourseForUser(user);
        Level level = createLevel(course, 1, false, 50, "Binary Search Basics");
        seedQuiz(level);
        seedFlashcard(level);
        CodingProblem codingProblem = seedCodingProblem(level);

        mockMvc.perform(get("/api/levels/{levelId}", level.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.levelId").value(level.getId().toString()))
                .andExpect(jsonPath("$.courseId").value(course.getId().toString()))
                .andExpect(jsonPath("$.courseTitle").value(course.getTitle()))
                .andExpect(jsonPath("$.orderNumber").value(1))
                .andExpect(jsonPath("$.title").value("Binary Search Basics"))
                .andExpect(jsonPath("$.xpReward").value(50))
                .andExpect(jsonPath("$.boss").doesNotExist())
                .andExpect(jsonPath("$.isBoss").value(false))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.unlocked").value(true))
                .andExpect(jsonPath("$.completedAt").isEmpty())
                .andExpect(jsonPath("$.quizQuestions[0].quizId").exists())
                .andExpect(jsonPath("$.quizQuestions[0].question").value("Binary search requires which input shape?"))
                .andExpect(jsonPath("$.quizQuestions[0].correctAnswer").doesNotExist())
                .andExpect(jsonPath("$.flashcards[0].front").value("When should you use binary search?"))
                .andExpect(jsonPath("$.codingProblems[0].problemId").value(codingProblem.getId().toString()))
                .andExpect(jsonPath("$.codingProblems[0].title").value("Find the Target Index"))
                .andExpect(jsonPath("$.codingProblems[0].sampleTestCases[0].stdin").value("5 7"))
                .andExpect(jsonPath("$.codingProblems[0].starterCode.java").exists())
                .andExpect(jsonPath("$.codingProblems[0].hiddenTests").doesNotExist())
                .andExpect(jsonPath("$.codingProblems[0].hidden_tests_json").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.secret").doesNotExist());
    }

    @Test
    void shouldAllowReadingCompletedLevelDetails() throws Exception {
        LoginResponse loginResponse = registerAndLogin("level-details-completed-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Level level = createLevelForUser(user, 1, false, 50);

        mockMvc.perform(post("/api/levels/{levelId}/complete", level.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/levels/{levelId}", level.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.unlocked").value(true))
                .andExpect(jsonPath("$.completedAt").exists());
    }

    @Test
    void shouldReturn403WhenReadingLockedLevelDetails() throws Exception {
        LoginResponse loginResponse = registerAndLogin("level-details-locked-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Course course = createCourseForUser(user);
        createLevel(course, 1, false, 50, "First Level");
        Level secondLevel = createLevel(course, 2, false, 75, "Second Level");

        mockMvc.perform(get("/api/levels/{levelId}", secondLevel.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("Complete previous levels before opening this lesson."))
                .andExpect(jsonPath("$.path").value("/api/levels/" + secondLevel.getId()));
    }

    @Test
    void shouldKeepLevelReadUnlockStateIndependentPerUser() throws Exception {
        LoginResponse firstLoginResponse = registerAndLogin("level-details-owner-" + System.currentTimeMillis() + "@example.com");
        LoginResponse secondLoginResponse = registerAndLogin("level-details-other-" + System.currentTimeMillis() + "@example.com");
        User firstUser = userRepository.findByEmail(firstLoginResponse.email()).orElseThrow();
        Course course = createCourseForUser(firstUser);
        Level firstLevel = createLevel(course, 1, false, 50, "First Level");
        Level secondLevel = createLevel(course, 2, false, 75, "Second Level");

        mockMvc.perform(post("/api/levels/{levelId}/complete", firstLevel.getId())
                        .header("Authorization", "Bearer " + firstLoginResponse.accessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/levels/{levelId}", secondLevel.getId())
                        .header("Authorization", "Bearer " + firstLoginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unlocked").value(true));

        mockMvc.perform(get("/api/levels/{levelId}", secondLevel.getId())
                        .header("Authorization", "Bearer " + secondLoginResponse.accessToken()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void shouldReturn404WhenReadingMissingLevelDetails() throws Exception {
        LoginResponse loginResponse = registerAndLogin("level-details-missing-" + System.currentTimeMillis() + "@example.com");
        UUID missingLevelId = UUID.randomUUID();

        mockMvc.perform(get("/api/levels/{levelId}", missingLevelId)
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Level not found."))
                .andExpect(jsonPath("$.path").value("/api/levels/" + missingLevelId));
    }

    @Test
    void shouldReturn401WhenReadingLevelDetailsWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/levels/{levelId}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

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

    private Quiz seedQuiz(Level level) {
        Instant now = Instant.now();
        return quizRepository.save(new Quiz(
                UUID.randomUUID(),
                level,
                1,
                "Binary search requires which input shape?",
                "Sorted data",
                "Random data",
                "Only linked lists",
                "No condition",
                "A",
                "Binary search works when the search space is sorted or monotonic.",
                "binary-search",
                20,
                now,
                now
        ));
    }

    private Flashcard seedFlashcard(Level level) {
        Instant now = Instant.now();
        return flashcardRepository.save(new Flashcard(
                UUID.randomUUID(),
                level,
                1,
                "When should you use binary search?",
                "When the search space is sorted or monotonic.",
                "binary-search",
                now,
                now
        ));
    }

    private CodingProblem seedCodingProblem(Level level) {
        Instant now = Instant.now();
        return codingProblemRepository.save(new CodingProblem(
                UUID.randomUUID(),
                level,
                "Find the Target Index",
                "Return the index of the target in a sorted array.",
                Map.of(
                        "java", "class Solution { }",
                        "python", "def solve():\n    pass"
                ),
                List.of(Map.of(
                        "stdin", "5 7",
                        "expectedOutput", "2"
                )),
                List.of(Map.of(
                        "stdin", "10 99",
                        "expectedOutput", "-1"
                )),
                "EASY",
                100,
                now,
                now
        ));
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
