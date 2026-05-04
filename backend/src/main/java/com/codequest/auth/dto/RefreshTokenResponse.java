package com.codequest.auth.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Integer expiresInSeconds
) {
}
