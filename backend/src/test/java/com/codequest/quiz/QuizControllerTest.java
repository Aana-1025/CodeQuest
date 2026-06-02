package com.codequest.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
class QuizControllerTest {

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
    private QuizRepository quizRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Test
    void shouldReturnIsCorrectTrueWhenAuthenticatedUserSubmitsCorrectAnswer() throws Exception {
        LoginResponse loginResponse = registerAndLogin("quizcorrect-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Quiz quiz = createQuizForUser(user, "C");
        long attemptCountBefore = quizAttemptRepository.countByQuizId(quiz.getId());
        int startingXp = user.getXp();

        mockMvc.perform(post("/api/quizzes/{quizQuestionId}/submit", quiz.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "selectedAnswer": "C"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizQuestionId").value(quiz.getId().toString()))
                .andExpect(jsonPath("$.selectedAnswer").value("C"))
                .andExpect(jsonPath("$.isCorrect").value(true))
                .andExpect(jsonPath("$.explanation").value("Correct answers should come from the backend only."))
                .andExpect(jsonPath("$.concept").value("Quiz Security"))
                .andExpect(jsonPath("$.weakConcepts").isArray())
                .andExpect(jsonPath("$.weakConcepts.length()").value(0))
                .andExpect(jsonPath("$.correctAnswer").doesNotExist())
                .andExpect(jsonPath("$.userId").doesNotExist())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.role").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist());

        assertEquals(attemptCountBefore + 1, quizAttemptRepository.countByQuizId(quiz.getId()));
        assertEquals(startingXp + quiz.getXpReward(), userRepository.findById(user.getId()).orElseThrow().getXp());
    }

    @Test
    void shouldReturnIsCorrectFalseWhenAuthenticatedUserSubmitsWrongAnswer() throws Exception {
        LoginResponse loginResponse = registerAndLogin("quizwrong-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Quiz quiz = createQuizForUser(user, "B");
        long attemptCountBefore = quizAttemptRepository.countByQuizId(quiz.getId());
        int startingXp = user.getXp();

        mockMvc.perform(post("/api/quizzes/{quizQuestionId}/submit", quiz.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "selectedAnswer": "A"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizQuestionId").value(quiz.getId().toString()))
                .andExpect(jsonPath("$.selectedAnswer").value("A"))
                .andExpect(jsonPath("$.isCorrect").value(false))
                .andExpect(jsonPath("$.explanation").value("Correct answers should come from the backend only."))
                .andExpect(jsonPath("$.concept").value("Quiz Security"))
                .andExpect(jsonPath("$.weakConcepts").isArray())
                .andExpect(jsonPath("$.weakConcepts[0]").value("Quiz Security"))
                .andExpect(jsonPath("$.correctAnswer").doesNotExist());

        assertEquals(attemptCountBefore + 1, quizAttemptRepository.countByQuizId(quiz.getId()));
        assertEquals(startingXp, userRepository.findById(user.getId()).orElseThrow().getXp());
    }

    @Test
    void shouldReturn404WhenQuizQuestionIsMissing() throws Exception {
        LoginResponse loginResponse = registerAndLogin("quizmissing-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        UUID missingQuizId = UUID.randomUUID();
        long attemptCountBefore = quizAttemptRepository.count();
        int startingXp = user.getXp();

        mockMvc.perform(post("/api/quizzes/{quizQuestionId}/submit", missingQuizId)
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "selectedAnswer": "B"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Quiz question not found."))
                .andExpect(jsonPath("$.path").value("/api/quizzes/" + missingQuizId + "/submit"));

        assertEquals(attemptCountBefore, quizAttemptRepository.count());
        assertEquals(startingXp, userRepository.findById(user.getId()).orElseThrow().getXp());
    }

    @Test
    void shouldReturn400WhenSelectedAnswerIsInvalid() throws Exception {
        LoginResponse loginResponse = registerAndLogin("quizinvalid-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Quiz quiz = createQuizForUser(user, "D");
        long attemptCountBefore = quizAttemptRepository.countByQuizId(quiz.getId());
        int startingXp = user.getXp();

        mockMvc.perform(post("/api/quizzes/{quizQuestionId}/submit", quiz.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "selectedAnswer": "Z"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        assertEquals(attemptCountBefore, quizAttemptRepository.countByQuizId(quiz.getId()));
        assertEquals(startingXp, userRepository.findById(user.getId()).orElseThrow().getXp());
    }

    @Test
    void shouldReturn401WhenSubmittingQuizAnswerWithoutAuthentication() throws Exception {
        long attemptCountBefore = quizAttemptRepository.count();

        mockMvc.perform(post("/api/quizzes/{quizQuestionId}/submit", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "selectedAnswer": "A"
                                }
                                """))
                .andExpect(status().isUnauthorized());

        assertEquals(attemptCountBefore, quizAttemptRepository.count());
    }

    @Test
    void shouldCreateRepeatedAttemptRowsForRepeatedSubmits() throws Exception {
        LoginResponse loginResponse = registerAndLogin("quizrepeat-" + System.currentTimeMillis() + "@example.com");
        User user = userRepository.findByEmail(loginResponse.email()).orElseThrow();
        Quiz quiz = createQuizForUser(user, "A");
        long attemptCountBefore = quizAttemptRepository.countByQuizId(quiz.getId());
        int startingXp = user.getXp();

        mockMvc.perform(post("/api/quizzes/{quizQuestionId}/submit", quiz.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "selectedAnswer": "A"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/quizzes/{quizQuestionId}/submit", quiz.getId())
                        .header("Authorization", "Bearer " + loginResponse.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "selectedAnswer": "A"
                                }
                                """))
                .andExpect(status().isOk());

        assertEquals(attemptCountBefore + 2, quizAttemptRepository.countByQuizId(quiz.getId()));
        assertEquals(startingXp + (quiz.getXpReward() * 2), userRepository.findById(user.getId()).orElseThrow().getXp());
    }

    @Test
    void shouldReturnAuthenticatedUsersQuizAttemptHistoryOrderedNewestFirstAndSafe() throws Exception {
        LoginResponse firstUserLogin = registerAndLogin("quizhistory-first-" + System.currentTimeMillis() + "@example.com");
        LoginResponse secondUserLogin = registerAndLogin("quizhistory-second-" + System.currentTimeMillis() + "@example.com");

        User firstUser = userRepository.findByEmail(firstUserLogin.email()).orElseThrow();
        User secondUser = userRepository.findByEmail(secondUserLogin.email()).orElseThrow();

        Quiz firstQuiz = createQuizForUser(firstUser, "B");
        Quiz secondQuiz = createQuizForUser(secondUser, "D");

        mockMvc.perform(post("/api/quizzes/{quizQuestionId}/submit", firstQuiz.getId())
                        .header("Authorization", "Bearer " + firstUserLogin.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "selectedAnswer": "A"
                                }
                                """))
                .andExpect(status().isOk());

        Thread.sleep(5);

        mockMvc.perform(post("/api/quizzes/{quizQuestionId}/submit", firstQuiz.getId())
                        .header("Authorization", "Bearer " + firstUserLogin.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "selectedAnswer": "B"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/quizzes/{quizQuestionId}/submit", secondQuiz.getId())
                        .header("Authorization", "Bearer " + secondUserLogin.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "selectedAnswer": "D"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/quizzes/attempts")
                        .header("Authorization", "Bearer " + firstUserLogin.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attempts.length()").value(2))
                .andExpect(jsonPath("$.attempts[0].quizQuestionId").value(firstQuiz.getId().toString()))
                .andExpect(jsonPath("$.attempts[0].selectedAnswer").value("B"))
                .andExpect(jsonPath("$.attempts[0].isCorrect").value(true))
                .andExpect(jsonPath("$.attempts[0].question").value("Which option is correct?"))
                .andExpect(jsonPath("$.attempts[0].concept").value("Quiz Security"))
                .andExpect(jsonPath("$.attempts[0].explanation").value("Correct answers should come from the backend only."))
                .andExpect(jsonPath("$.attempts[0].levelId").isNotEmpty())
                .andExpect(jsonPath("$.attempts[0].levelTitle").value("Quiz Submit Level"))
                .andExpect(jsonPath("$.attempts[0].courseId").isNotEmpty())
                .andExpect(jsonPath("$.attempts[0].courseTitle").value("Quiz Submit Course"))
                .andExpect(jsonPath("$.attempts[0].correctAnswer").doesNotExist())
                .andExpect(jsonPath("$.attempts[0].userId").doesNotExist())
                .andExpect(jsonPath("$.attempts[0].token").doesNotExist())
                .andExpect(jsonPath("$.attempts[0].password").doesNotExist())
                .andExpect(jsonPath("$.attempts[0].role").doesNotExist())
                .andExpect(jsonPath("$.attempts[0].refreshToken").doesNotExist())
                .andExpect(jsonPath("$.attempts[1].selectedAnswer").value("A"))
                .andExpect(jsonPath("$.attempts[1].quizQuestionId").value(firstQuiz.getId().toString()));
    }

    @Test
    void shouldReturnEmptyQuizAttemptHistoryForAuthenticatedUserWithoutAttempts() throws Exception {
        LoginResponse loginResponse = registerAndLogin("quizhistory-empty-" + System.currentTimeMillis() + "@example.com");

        mockMvc.perform(get("/api/quizzes/attempts")
                        .header("Authorization", "Bearer " + loginResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attempts.length()").value(0));
    }

    @Test
    void shouldReturn401WhenFetchingQuizAttemptHistoryWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/quizzes/attempts"))
                .andExpect(status().isUnauthorized());
    }

    private Quiz createQuizForUser(User user, String correctAnswer) {
        Instant now = Instant.now();
        Course course = new Course(
                UUID.randomUUID(),
                "quiz-submit-topic-" + UUID.randomUUID(),
                "Quiz Submit Course",
                "A seeded course for quiz submission tests.",
                user,
                CourseDifficulty.BEGINNER,
                false,
                50,
                CourseSourceType.AI,
                now,
                now
        );
        courseRepository.save(course);

        Level level = new Level(
                UUID.randomUUID(),
                course,
                "Quiz Submit Level",
                "# Quiz Submit Level",
                1,
                false,
                50,
                now,
                now
        );
        levelRepository.save(level);

        Quiz quiz = new Quiz(
                UUID.randomUUID(),
                level,
                1,
                "Which option is correct?",
                "Option A",
                "Option B",
                "Option C",
                "Option D",
                correctAnswer,
                "Correct answers should come from the backend only.",
                "Quiz Security",
                20,
                now,
                now
        );
        return quizRepository.save(quiz);
    }

    private LoginResponse registerAndLogin(String email) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("Quiz Test", email, "QuizPass123");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(email, "QuizPass123");
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
