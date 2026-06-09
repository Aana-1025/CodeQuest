package com.codequest.leaderboard.dto;

import java.util.UUID;

import com.codequest.user.UserRank;

public record CurrentUserLeaderboardResponse(
        long rankPosition,
        UUID userId,
        int xp,
        UserRank rank
) {
}
