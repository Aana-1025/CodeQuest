package com.codequest.leaderboard.dto;

import java.util.List;

public record LeaderboardResponse(
        int page,
        int size,
        String period,
        long totalItems,
        int totalPages,
        List<LeaderboardItemResponse> items,
        CurrentUserLeaderboardResponse currentUser
) {
}
