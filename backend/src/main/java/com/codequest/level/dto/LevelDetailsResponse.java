package com.codequest.level.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.codequest.flashcard.dto.FlashcardResponse;
import com.codequest.problem.dto.CodingProblemResponse;
import com.codequest.quiz.dto.QuizQuestionResponse;

public record LevelDetailsResponse(
        UUID levelId,
        UUID courseId,
        String courseTitle,
        int orderNumber,
        String title,
        String contentMarkdown,
        int xpReward,
        boolean isBoss,
        boolean completed,
        boolean unlocked,
        Instant completedAt,
        List<QuizQuestionResponse> quizQuestions,
        List<FlashcardResponse> flashcards,
        List<CodingProblemResponse> codingProblems
) {
}
