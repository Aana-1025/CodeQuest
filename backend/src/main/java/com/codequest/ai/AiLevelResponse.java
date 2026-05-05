package com.codequest.ai;

import java.util.List;

public record AiLevelResponse(
        String title,
        String contentMarkdown,
        Integer orderNumber,
        Boolean isBoss,
        Integer xpReward,
        List<AiFlashcardResponse> flashcards,
        List<AiQuizQuestionResponse> quiz,
        List<AiCodingProblemResponse> codingProblems
) {
}
