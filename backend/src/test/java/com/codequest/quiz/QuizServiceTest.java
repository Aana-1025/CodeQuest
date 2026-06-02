package com.codequest.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.level.Level;
import com.codequest.progress.XPService;
import com.codequest.quiz.dto.QuizAttemptHistoryItemResponse;
import com.codequest.quiz.dto.QuizAttemptHistoryResponse;
import com.codequest.quiz.dto.SubmitQuizAnswerResponse;
import com.codequest.course.Course;
import com.codequest.course.CourseDifficulty;
import com.codequest.course.CourseSourceType;
import com.codequest.user.User;
import com.codequest.user.UserRank;
import com.codequest.user.UserRepository;
import com.codequest.user.UserRole;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private XPService xpService;

    private QuizService quizService;

    @BeforeEach
    void setUp() {
        quizService = new QuizService(quizRepository, quizAttemptRepository, userRepository, xpService);
    }

    @Test
    void submitAnswer_shouldReturnIsCorrectTrueWhenSelectedAnswerMatchesCorrectAnswer() {
        Quiz quiz = createQuiz("B", "Binary search halves the search space.", "Binary Search");
        User user = createUser();
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(xpService.addXpAndRecalculateRank(user, 20)).thenAnswer(invocation -> {
            user.setXp(20);
            user.setRank(UserRank.BEGINNER);
            return user;
        });

        SubmitQuizAnswerResponse response = quizService.submitAnswer(user.getId(), quiz.getId(), "B");

        assertEquals(quiz.getId(), response.quizQuestionId());
        assertEquals("B", response.selectedAnswer());
        assertTrue(response.isCorrect());
        assertEquals("Binary search halves the search space.", response.explanation());
        assertEquals("Binary Search", response.concept());
        assertEquals(20, user.getXp());
        verify(quizAttemptRepository).save(any(QuizAttempt.class));
    }

    @Test
    void submitAnswer_shouldReturnIsCorrectFalseWhenSelectedAnswerDoesNotMatchCorrectAnswer() {
        Quiz quiz = createQuiz("C", "Two pointers move toward each other.", "Two Pointers");
        User user = createUser();
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        SubmitQuizAnswerResponse response = quizService.submitAnswer(user.getId(), quiz.getId(), "A");

        assertEquals("A", response.selectedAnswer());
        assertFalse(response.isCorrect());
        assertEquals("Two pointers move toward each other.", response.explanation());
        assertEquals("Two Pointers", response.concept());
        assertEquals(0, user.getXp());
        verify(quizAttemptRepository).save(any(QuizAttempt.class));
    }

    @Test
    void submitAnswer_shouldNormalizeLowercaseAndWhitespaceSelectedAnswer() {
        Quiz quiz = createQuiz("D", "A stack uses LIFO ordering.", "Stacks");
        User user = createUser();
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(xpService.addXpAndRecalculateRank(user, 20)).thenAnswer(invocation -> {
            user.setXp(20);
            user.setRank(UserRank.BEGINNER);
            return user;
        });

        SubmitQuizAnswerResponse response = quizService.submitAnswer(user.getId(), quiz.getId(), "  d ");

        assertEquals("D", response.selectedAnswer());
        assertTrue(response.isCorrect());
    }

    @Test
    void submitAnswer_shouldThrowNotFoundWhenQuizQuestionDoesNotExist() {
        UUID missingQuizId = UUID.randomUUID();
        when(quizRepository.findById(missingQuizId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(
                ApiException.class,
                () -> quizService.submitAnswer(UUID.randomUUID(), missingQuizId, "B")
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        assertEquals("Quiz question not found.", exception.getMessage());
        verify(quizAttemptRepository, never()).save(any(QuizAttempt.class));
    }

    @Test
    void submitAnswer_shouldNotExposeCorrectAnswerThroughResponseDto() {
        Quiz quiz = createQuiz("A", null, null);
        User user = createUser();
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(xpService.addXpAndRecalculateRank(user, 20)).thenAnswer(invocation -> {
            user.setXp(20);
            user.setRank(UserRank.BEGINNER);
            return user;
        });

        SubmitQuizAnswerResponse response = quizService.submitAnswer(user.getId(), quiz.getId(), "A");

        assertNotNull(response.quizQuestionId());
        assertEquals("A", response.selectedAnswer());
        assertTrue(response.isCorrect());
        assertNull(response.explanation());
        assertNull(response.concept());
    }

    @Test
    void submitAnswer_shouldPersistOneAttemptForSuccessfulSubmit() {
        Quiz quiz = createQuiz("B", "Binary search halves the search space.", "Binary Search");
        User user = createUser();
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(xpService.addXpAndRecalculateRank(user, 20)).thenAnswer(invocation -> {
            user.setXp(20);
            user.setRank(UserRank.BEGINNER);
            return user;
        });

        quizService.submitAnswer(user.getId(), quiz.getId(), "B");

        verify(quizAttemptRepository, times(1)).save(any(QuizAttempt.class));
    }

    @Test
    void submitAnswer_shouldCreateMultipleAttemptsForRepeatedSubmits() {
        Quiz quiz = createQuiz("B", "Binary search halves the search space.", "Binary Search");
        User user = createUser();
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        quizService.submitAnswer(user.getId(), quiz.getId(), "B");
        quizService.submitAnswer(user.getId(), quiz.getId(), "A");

        verify(quizAttemptRepository, times(2)).save(any(QuizAttempt.class));
    }

    @Test
    void submitAnswer_shouldAwardXpWhenAnswerIsCorrect() {
        Quiz quiz = createQuiz("B", "Binary search halves the search space.", "Binary Search");
        User user = createUser();
        user.setXp(15);
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(xpService.addXpAndRecalculateRank(user, 20)).thenAnswer(invocation -> {
            user.setXp(35);
            user.setRank(UserRank.BEGINNER);
            return user;
        });

        quizService.submitAnswer(user.getId(), quiz.getId(), "B");

        assertEquals(35, user.getXp());
    }

    @Test
    void submitAnswer_shouldNotAwardXpWhenAnswerIsIncorrect() {
        Quiz quiz = createQuiz("B", "Binary search halves the search space.", "Binary Search");
        User user = createUser();
        user.setXp(15);
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        quizService.submitAnswer(user.getId(), quiz.getId(), "A");

        assertEquals(15, user.getXp());
    }

    @Test
    void submitAnswer_shouldAwardXpAgainForRepeatedCorrectSubmitsInMvp() {
        Quiz quiz = createQuiz("B", "Binary search halves the search space.", "Binary Search");
        User user = createUser();
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(xpService.addXpAndRecalculateRank(user, 20)).thenAnswer(invocation -> {
            int updatedXp = (user.getXp() == null ? 0 : user.getXp()) + 20;
            user.setXp(updatedXp);
            user.setRank(UserRank.BEGINNER);
            return user;
        });

        quizService.submitAnswer(user.getId(), quiz.getId(), "B");
        quizService.submitAnswer(user.getId(), quiz.getId(), "B");

        assertEquals(40, user.getXp());
    }

    @Test
    void submitAnswer_shouldNormalizeSelectedAnswerBeforePersistence() {
        Quiz quiz = createQuiz("C", "Queues are FIFO.", "Queues");
        User user = createUser();
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(xpService.addXpAndRecalculateRank(user, 20)).thenAnswer(invocation -> {
            user.setXp(20);
            user.setRank(UserRank.BEGINNER);
            return user;
        });

        org.mockito.ArgumentCaptor<QuizAttempt> attemptCaptor = org.mockito.ArgumentCaptor.forClass(QuizAttempt.class);
        when(quizAttemptRepository.save(attemptCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        quizService.submitAnswer(user.getId(), quiz.getId(), "  c ");

        assertEquals("C", attemptCaptor.getValue().getSelectedAnswer());
        assertTrue(attemptCaptor.getValue().isCorrect());
        assertSame(user, attemptCaptor.getValue().getUser());
        assertSame(quiz, attemptCaptor.getValue().getQuiz());
    }

    @Test
    void submitAnswer_shouldRejectInvalidAnswerAndNotPersistAttempt() {
        Quiz quiz = createQuiz("A", "Explanation", "Concept");
        User user = createUser();
        user.setXp(12);
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> quizService.submitAnswer(user.getId(), quiz.getId(), "Z")
        );

        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("Selected answer must be one of A, B, C, or D.", exception.getMessage());
        assertEquals(12, user.getXp());
        verify(userRepository, never()).getReferenceById(any(UUID.class));
        verify(quizAttemptRepository, never()).save(any(QuizAttempt.class));
    }

    @Test
    void submitAnswer_shouldTreatNullUserXpAsZeroWhenAwardingXp() {
        Quiz quiz = createQuiz("B", "Binary search halves the search space.", "Binary Search");
        User user = createUser();
        user.setXp(null);
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(xpService.addXpAndRecalculateRank(user, 20)).thenAnswer(invocation -> {
            user.setXp(20);
            user.setRank(UserRank.BEGINNER);
            return user;
        });

        quizService.submitAnswer(user.getId(), quiz.getId(), "B");

        assertEquals(20, user.getXp());
    }

    @Test
    void submitAnswer_shouldRecalculateRankWhenCorrectAnswerCrossesThreshold() {
        Quiz quiz = createQuiz("B", "Binary search halves the search space.", "Binary Search");
        User user = createUser();
        user.setXp(490);
        user.setRank(UserRank.BEGINNER);
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(xpService.addXpAndRecalculateRank(user, 20)).thenAnswer(invocation -> {
            user.setXp(510);
            user.setRank(UserRank.CODER);
            return user;
        });

        SubmitQuizAnswerResponse response = quizService.submitAnswer(user.getId(), quiz.getId(), "B");

        assertTrue(response.isCorrect());
        assertEquals(510, user.getXp());
        assertEquals(UserRank.CODER, user.getRank());
    }

    @Test
    void getAttemptHistory_shouldReturnCurrentUserAttemptsOrderedNewestFirst() {
        User user = createUser();
        QuizAttempt newestAttempt = createAttempt(user, "A", true, Instant.parse("2026-05-15T10:16:30Z"), "Newest Question", "Newest Concept", "Newest Explanation", "Newest Level", "Newest Course");
        QuizAttempt olderAttempt = createAttempt(user, "C", false, Instant.parse("2026-05-15T09:16:30Z"), "Older Question", "Older Concept", "Older Explanation", "Older Level", "Older Course");
        when(quizAttemptRepository.findByUserIdOrderByAttemptedAtDesc(user.getId())).thenReturn(List.of(newestAttempt, olderAttempt));

        QuizAttemptHistoryResponse response = quizService.getAttemptHistory(user.getId());

        assertEquals(2, response.attempts().size());
        assertIterableEquals(
                List.of(newestAttempt.getId(), olderAttempt.getId()),
                response.attempts().stream().map(QuizAttemptHistoryItemResponse::attemptId).toList()
        );
        assertEquals("Newest Question", response.attempts().get(0).question());
        assertEquals("Newest Level", response.attempts().get(0).levelTitle());
        assertEquals("Newest Course", response.attempts().get(0).courseTitle());
    }

    @Test
    void getAttemptHistory_shouldReturnOnlyCurrentUserAttempts() {
        User currentUser = createUser();
        User anotherUser = createUser();
        QuizAttempt currentUserAttempt = createAttempt(currentUser, "B", true, Instant.parse("2026-05-15T11:16:30Z"), "Current User Question", "Concept", "Explanation", "Level", "Course");
        QuizAttempt anotherUserAttempt = createAttempt(anotherUser, "D", false, Instant.parse("2026-05-15T12:16:30Z"), "Another User Question", "Other Concept", "Other Explanation", "Other Level", "Other Course");
        when(quizAttemptRepository.findByUserIdOrderByAttemptedAtDesc(currentUser.getId())).thenReturn(List.of(currentUserAttempt));

        QuizAttemptHistoryResponse response = quizService.getAttemptHistory(currentUser.getId());

        assertEquals(1, response.attempts().size());
        assertEquals(currentUserAttempt.getId(), response.attempts().get(0).attemptId());
        assertFalse(response.attempts().stream().anyMatch(attempt -> anotherUserAttempt.getId().equals(attempt.attemptId())));
        verify(quizAttemptRepository).findByUserIdOrderByAttemptedAtDesc(currentUser.getId());
    }

    @Test
    void getAttemptHistory_shouldReturnEmptyAttemptsWhenUserHasNoHistory() {
        UUID userId = UUID.randomUUID();
        when(quizAttemptRepository.findByUserIdOrderByAttemptedAtDesc(userId)).thenReturn(List.of());

        QuizAttemptHistoryResponse response = quizService.getAttemptHistory(userId);

        assertTrue(response.attempts().isEmpty());
    }

    @Test
    void getAttemptHistory_shouldMapSafeUsefulFieldsWithoutCorrectAnswer() {
        User user = createUser();
        QuizAttempt attempt = createAttempt(user, "C", false, Instant.parse("2026-05-15T13:16:30Z"), "Safe Question", "Safe Concept", "Safe Explanation", "Safe Level", "Safe Course");
        when(quizAttemptRepository.findByUserIdOrderByAttemptedAtDesc(user.getId())).thenReturn(List.of(attempt));

        QuizAttemptHistoryResponse response = quizService.getAttemptHistory(user.getId());
        QuizAttemptHistoryItemResponse item = response.attempts().get(0);

        assertEquals(attempt.getId(), item.attemptId());
        assertEquals(attempt.getQuiz().getId(), item.quizQuestionId());
        assertEquals("C", item.selectedAnswer());
        assertFalse(item.isCorrect());
        assertEquals(Instant.parse("2026-05-15T13:16:30Z"), item.attemptedAt());
        assertEquals("Safe Question", item.question());
        assertEquals("Safe Concept", item.concept());
        assertEquals("Safe Explanation", item.explanation());
        assertEquals(attempt.getQuiz().getLevel().getId(), item.levelId());
        assertEquals("Safe Level", item.levelTitle());
        assertEquals(attempt.getQuiz().getLevel().getCourse().getId(), item.courseId());
        assertEquals("Safe Course", item.courseTitle());
    }

    private Quiz createQuiz(String correctAnswer, String explanation, String conceptTag) {
        Instant now = Instant.now();
        Level level = new Level(
                UUID.randomUUID(),
                null,
                "Quiz Test Level",
                "# Quiz Test Level",
                1,
                false,
                50,
                now,
                now
        );

        return new Quiz(
                UUID.randomUUID(),
                level,
                1,
                "Which option is correct?",
                "Option A",
                "Option B",
                "Option C",
                "Option D",
                correctAnswer,
                explanation,
                conceptTag,
                20,
                now,
                now
        );
    }

    private User createUser() {
        Instant now = Instant.now();
        User user = new User(UUID.randomUUID(), "Quiz User", "quiz-user@example.com", "hashed-password");
        user.setRank(UserRank.BEGINNER);
        user.setRole(UserRole.STUDENT);
        user.setXp(0);
        user.setStreak(0);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }

    private QuizAttempt createAttempt(User user, String selectedAnswer, boolean isCorrect, Instant attemptedAt,
                                      String question, String conceptTag, String explanation, String levelTitle,
                                      String courseTitle) {
        Course course = new Course(
                UUID.randomUUID(),
                "attempt-history-topic-" + UUID.randomUUID(),
                courseTitle,
                "Course description",
                user,
                CourseDifficulty.BEGINNER,
                false,
                50,
                CourseSourceType.AI,
                attemptedAt.minusSeconds(60),
                attemptedAt.minusSeconds(60)
        );
        Level level = new Level(
                UUID.randomUUID(),
                course,
                levelTitle,
                "# " + levelTitle,
                1,
                false,
                50,
                attemptedAt.minusSeconds(30),
                attemptedAt.minusSeconds(30)
        );
        Quiz quiz = new Quiz(
                UUID.randomUUID(),
                level,
                1,
                question,
                "Option A",
                "Option B",
                "Option C",
                "Option D",
                "A",
                explanation,
                conceptTag,
                20,
                attemptedAt.minusSeconds(15),
                attemptedAt.minusSeconds(15)
        );

        return new QuizAttempt(
                UUID.randomUUID(),
                user,
                quiz,
                selectedAnswer,
                isCorrect,
                attemptedAt
        );
    }
}
