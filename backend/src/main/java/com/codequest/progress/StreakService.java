package com.codequest.progress;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codequest.user.User;
import com.codequest.user.UserRepository;

@Service
public class StreakService {

    private static final int DAILY_LOGIN_XP = 30;

    private final XPService xpService;
    private final UserRepository userRepository;
    private final Clock clock;

    @Autowired
    public StreakService(XPService xpService, UserRepository userRepository) {
        this(xpService, userRepository, Clock.systemDefaultZone());
    }

    StreakService(XPService xpService, UserRepository userRepository, Clock clock) {
        this.xpService = xpService;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    public User applySuccessfulLogin(User user) {
        Instant now = Instant.now(clock);
        Instant previousLogin = user.getLastLogin();
        int safeCurrentStreak = sanitizeStreak(user.getStreak());

        if (previousLogin == null) {
            user.setStreak(1);
            user.setLastLogin(now);
            user.setUpdatedAt(now);
            return xpService.addXpAndRecalculateRank(user, DAILY_LOGIN_XP);
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate previousLoginDate = LocalDate.ofInstant(previousLogin, clock.getZone());

        if (previousLoginDate.isEqual(today)) {
            if (user.getStreak() == null || user.getStreak() < 1) {
                user.setStreak(safeCurrentStreak);
                user.setUpdatedAt(now);
                return userRepository.save(user);
            }

            return user;
        }

        LocalDate yesterday = today.minusDays(1);
        int updatedStreak = previousLoginDate.isEqual(yesterday) ? safeCurrentStreak + 1 : 1;

        user.setStreak(updatedStreak);
        user.setLastLogin(now);
        user.setUpdatedAt(now);
        return xpService.addXpAndRecalculateRank(user, DAILY_LOGIN_XP);
    }

    private int sanitizeStreak(Integer streak) {
        return streak == null || streak < 1 ? 1 : streak;
    }
}
