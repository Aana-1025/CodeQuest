package com.codequest.progress.dto;

import java.time.Instant;
import java.util.UUID;

public record LevelCompletionResponse(
        UUID levelId,
        boolean completed,
        boolean alreadyCompleted,
        int xpAwarded,
        int totalXp,
        Instant completedAt
) {
}
