package com.codequest.user;

import org.springframework.stereotype.Component;

import com.codequest.user.dto.UserProfileResponse;

@Component
public class UserMapper {

    public UserProfileResponse toUserProfileResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRank(),
                user.getXp(),
                user.getStreak(),
                user.getGoal(),
                user.getAvatarUrl(),
                user.getCreatedAt()
        );
    }
}