package com.codequest.auth.dto;

import java.util.UUID;

import com.codequest.user.UserRank;

public record LoginResponse(
        UUID userId,
        String name,
        String email,
        UserRank rank,
        Integer xp,
        Integer streak,
        String accessToken,
        String refreshToken,
        String tokenType,
        Integer expiresInSeconds
) {
}