package com.codequest.progress.dto;

import java.time.Instant;
import java.util.UUID;

public record LevelProgressResponse(
        UUID levelId,
        int orderNumber,
        String title,
        boolean isBoss,
        int xpReward,
        boolean completed,
        boolean unlocked,
        Instant completedAt
) {
}
