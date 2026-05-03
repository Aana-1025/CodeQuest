package com.codequest.auth.dto;

import com.codequest.user.UserRank;

import java.util.UUID;

public record RegisterResponse(
        UUID userId,
        String name,
        String email,
        UserRank rank,
        Integer xp
) {
}