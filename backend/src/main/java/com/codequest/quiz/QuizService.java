package com.codequest.quiz;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.quiz.dto.SubmitQuizAnswerResponse;
import com.codequest.user.User;
import com.codequest.user.UserRepository;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;

    public QuizService(QuizRepository quizRepository, QuizAttemptRepository quizAttemptRepository, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SubmitQuizAnswerResponse submitAnswer(UUID userId, UUID quizQuestionId, String selectedAnswer) {
        Quiz quiz = quizRepository.findById(quizQuestionId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Quiz question not found."));

        String normalizedSelectedAnswer = normalizeSelectedAnswer(selectedAnswer);
        if (!isValidSelectedAnswer(normalizedSelectedAnswer)) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Selected answer must be one of A, B, C, or D.");
        }

        boolean isCorrect = normalizedSelectedAnswer.equals(quiz.getCorrectAnswer());
        User user = userRepository.getReferenceById(userId);

        QuizAttempt attempt = new QuizAttempt(
                UUID.randomUUID(),
                user,
                quiz,
                normalizedSelectedAnswer,
                isCorrect,
                Instant.now()
        );
        quizAttemptRepository.save(attempt);

        return new SubmitQuizAnswerResponse(
                quiz.getId(),
                normalizedSelectedAnswer,
                isCorrect,
                quiz.getExplanation(),
                quiz.getConceptTag()
        );
    }

    private String normalizeSelectedAnswer(String selectedAnswer) {
        return selectedAnswer.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isValidSelectedAnswer(String selectedAnswer) {
        return "A".equals(selectedAnswer)
                || "B".equals(selectedAnswer)
                || "C".equals(selectedAnswer)
                || "D".equals(selectedAnswer);
    }
}
