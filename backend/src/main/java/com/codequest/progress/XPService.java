package com.codequest.progress;

import org.springframework.stereotype.Service;

import com.codequest.user.User;
import com.codequest.user.UserRank;
import com.codequest.user.UserRepository;

@Service
public class XPService {

    private final UserRepository userRepository;

    public XPService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addXpAndRecalculateRank(User user, int xpToAdd) {
        if (xpToAdd < 0) {
            throw new IllegalArgumentException("XP to add cannot be negative.");
        }

        int currentXp = currentXp(user);
        int updatedXp = currentXp + xpToAdd;

        user.setXp(updatedXp);
        user.setRank(calculateRankForXp(updatedXp));

        return userRepository.save(user);
    }

    UserRank calculateRankForXp(int totalXp) {
        if (totalXp < 0) {
            throw new IllegalArgumentException("Total XP cannot be negative.");
        }

        if (totalXp >= 25000) {
            return UserRank.LEGEND;
        }

        if (totalXp >= 12000) {
            return UserRank.ARCHITECT;
        }

        if (totalXp >= 5000) {
            return UserRank.ENGINEER;
        }

        if (totalXp >= 2000) {
            return UserRank.DEVELOPER;
        }

        if (totalXp >= 500) {
            return UserRank.CODER;
        }

        return UserRank.BEGINNER;
    }

    private int currentXp(User user) {
        return user.getXp() == null ? 0 : user.getXp();
    }
}
