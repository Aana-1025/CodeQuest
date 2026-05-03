package com.codequest.auth.mapper;

import com.codequest.auth.dto.RegisterResponse;
import com.codequest.user.User;
import org.springframework.stereotype.Component;

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
}