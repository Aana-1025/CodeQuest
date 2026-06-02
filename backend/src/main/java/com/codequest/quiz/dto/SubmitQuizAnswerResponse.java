package com.codequest.quiz.dto;

import java.util.List;
import java.util.UUID;

public record SubmitQuizAnswerResponse(
        UUID quizQuestionId,
        String selectedAnswer,
        boolean isCorrect,
        String explanation,
        String concept,
        List<String> weakConcepts
) {
}
