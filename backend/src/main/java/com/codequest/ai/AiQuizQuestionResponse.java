package com.codequest.ai;

public record AiQuizQuestionResponse(
        String question,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String correctAnswer,
        String explanation,
        String conceptTag,
        Integer xpReward
) {
}
