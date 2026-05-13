package com.codequest.course.dto;

import java.util.List;
import java.util.UUID;

import com.codequest.quiz.dto.QuizQuestionResponse;

public record CourseLevelResponse(
        UUID levelId,
        int orderNumber,
        String title,
        String contentMarkdown,
        int xpReward,
        boolean isBoss,
        List<QuizQuestionResponse> quizQuestions
) {
}
