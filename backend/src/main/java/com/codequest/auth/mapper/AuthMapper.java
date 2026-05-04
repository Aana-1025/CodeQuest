package com.codequest.auth.mapper;

import org.springframework.stereotype.Component;

import com.codequest.auth.dto.LoginResponse;
import com.codequest.auth.dto.RegisterResponse;
import com.codequest.user.User;

@Component
public class AuthMapper {

    public RegisterResponse toRegisterResponse(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRank(),
                user.getXp()
        );
    }

    public LoginResponse toLoginResponse(User user) {
        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRank(),
                user.getXp(),
                user.getStreak(),
                null,
                null,
                null
        );
    }

    public LoginResponse toLoginResponse(User user, String accessToken, int expiresInSeconds) {
        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRank(),
                user.getXp(),
                user.getStreak(),
                accessToken,
                "Bearer",
                expiresInSeconds
        );
    }
}