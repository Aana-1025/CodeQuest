package com.codequest.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.codequest.quiz.dto.SubmitQuizAnswerResponse;
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

    private QuizService quizService;

    @BeforeEach
    void setUp() {
        quizService = new QuizService(quizRepository, quizAttemptRepository, userRepository);
    }

    @Test
    void submitAnswer_shouldReturnIsCorrectTrueWhenSelectedAnswerMatchesCorrectAnswer() {
        Quiz quiz = createQuiz("B", "Binary search halves the search space.", "Binary Search");
        User user = createUser();
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        SubmitQuizAnswerResponse response = quizService.submitAnswer(user.getId(), quiz.getId(), "B");

        assertEquals(quiz.getId(), response.quizQuestionId());
        assertEquals("B", response.selectedAnswer());
        assertTrue(response.isCorrect());
        assertEquals("Binary search halves the search space.", response.explanation());
        assertEquals("Binary Search", response.concept());
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
        verify(quizAttemptRepository).save(any(QuizAttempt.class));
    }

    @Test
    void submitAnswer_shouldNormalizeLowercaseAndWhitespaceSelectedAnswer() {
        Quiz quiz = createQuiz("D", "A stack uses LIFO ordering.", "Stacks");
        User user = createUser();
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

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
    void submitAnswer_shouldNormalizeSelectedAnswerBeforePersistence() {
        Quiz quiz = createQuiz("C", "Queues are FIFO.", "Queues");
        User user = createUser();
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

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
        when(quizRepository.findById(quiz.getId())).thenReturn(Optional.of(quiz));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> quizService.submitAnswer(user.getId(), quiz.getId(), "Z")
        );

        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("Selected answer must be one of A, B, C, or D.", exception.getMessage());
        verify(userRepository, never()).getReferenceById(any(UUID.class));
        verify(quizAttemptRepository, never()).save(any(QuizAttempt.class));
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
}
