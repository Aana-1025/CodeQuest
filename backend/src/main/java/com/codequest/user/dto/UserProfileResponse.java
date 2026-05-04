package com.codequest.user.dto;

import com.codequest.user.UserRank;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID userId,
        String name,
        String email,
        UserRank rank,
        Integer xp,
        Integer streak,
        String goal,
        String avatarUrl,
        Instant createdAt
) {
}