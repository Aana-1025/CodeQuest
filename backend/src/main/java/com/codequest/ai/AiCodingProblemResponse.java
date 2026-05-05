package com.codequest.ai;

public record AiCodingProblemResponse(
        String title,
        String description,
        String difficulty,
        Integer xpReward
) {
}
