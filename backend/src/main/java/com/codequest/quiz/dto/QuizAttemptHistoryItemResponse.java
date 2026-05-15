package com.codequest.quiz.dto;

import java.time.Instant;
import java.util.UUID;

public record QuizAttemptHistoryItemResponse(
        UUID attemptId,
        UUID quizQuestionId,
        String selectedAnswer,
        boolean isCorrect,
        Instant attemptedAt,
        String question,
        String concept,
        String explanation,
        UUID levelId,
        String levelTitle,
        UUID courseId,
        String courseTitle
) {
}
