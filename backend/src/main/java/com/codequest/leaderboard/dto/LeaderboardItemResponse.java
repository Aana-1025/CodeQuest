package com.codequest.leaderboard.dto;

import java.util.UUID;

import com.codequest.user.UserRank;

public record LeaderboardItemResponse(
        long rankPosition,
        UUID userId,
        String name,
        int xp,
        UserRank rank,
        int streak
) {
}
