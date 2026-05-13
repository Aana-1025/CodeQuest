package com.codequest.quiz.dto;

import java.util.UUID;

public record QuizQuestionResponse(
        UUID quizId,
        int orderNumber,
        String question,
        QuizOptionsResponse options,
        String explanation,
        String conceptTag,
        int xpReward
) {
}
