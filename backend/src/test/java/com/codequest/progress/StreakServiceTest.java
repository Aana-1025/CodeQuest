package com.codequest.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codequest.user.User;
import com.codequest.user.UserRank;
import com.codequest.user.UserRepository;
import com.codequest.user.UserRole;

@ExtendWith(MockitoExtension.class)
class StreakServiceTest {

    @Mock
    private XPService xpService;

    @Mock
    private UserRepository userRepository;

    private StreakService streakService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-02T10:15:30Z"), ZoneId.of("UTC"));
        streakService = new StreakService(xpService, userRepository, clock);
    }

    @Test
    void applySuccessfulLogin_shouldInitializeFirstLoginAndAwardDailyXp() {
        User user = createUser();
        when(xpService.addXpAndRecalculateRank(user, 30)).thenAnswer(invocation -> {
            user.setXp(30);
            user.setRank(UserRank.BEGINNER);
            return user;
        });

        User updatedUser = streakService.applySuccessfulLogin(user);

        assertSame(user, updatedUser);
        assertEquals(1, updatedUser.getStreak());
        assertEquals(30, updatedUser.getXp());
        assertEquals(UserRank.BEGINNER, updatedUser.getRank());
        assertNotNull(updatedUser.getLastLogin());
        verify(xpService).addXpAndRecalculateRank(user, 30);
        verify(userRepository, never()).save(user);
    }

    @Test
    void applySuccessfulLogin_shouldNotAwardXpOrIncrementStreakTwiceOnSameDay() {
        User user = createUser();
        user.setXp(30);
        user.setStreak(1);
        user.setLastLogin(Instant.parse("2026-06-02T08:00:00Z"));

        User updatedUser = streakService.applySuccessfulLogin(user);

        assertSame(user, updatedUser);
        assertEquals(30, updatedUser.getXp());
        assertEquals(1, updatedUser.getStreak());
        assertEquals(Instant.parse("2026-06-02T08:00:00Z"), updatedUser.getLastLogin());
        verify(xpService, never()).addXpAndRecalculateRank(user, 30);
        verify(userRepository, never()).save(user);
    }

    @Test
    void applySuccessfulLogin_shouldIncrementStreakAndAwardXpForNextDayLogin() {
        User user = createUser();
        user.setXp(30);
        user.setStreak(1);
        user.setLastLogin(Instant.parse("2026-06-01T07:00:00Z"));
        when(xpService.addXpAndRecalculateRank(user, 30)).thenAnswer(invocation -> {
            user.setXp(60);
            user.setRank(UserRank.BEGINNER);
            return user;
        });

        User updatedUser = streakService.applySuccessfulLogin(user);

        assertEquals(2, updatedUser.getStreak());
        assertEquals(60, updatedUser.getXp());
        assertEquals(UserRank.BEGINNER, updatedUser.getRank());
        assertEquals(Instant.parse("2026-06-02T10:15:30Z"), updatedUser.getLastLogin());
        verify(xpService).addXpAndRecalculateRank(user, 30);
    }

    @Test
    void applySuccessfulLogin_shouldResetStreakAfterGapAndAwardXp() {
        User user = createUser();
        user.setXp(90);
        user.setStreak(5);
        user.setLastLogin(Instant.parse("2026-05-30T07:00:00Z"));
        when(xpService.addXpAndRecalculateRank(user, 30)).thenAnswer(invocation -> {
            user.setXp(120);
            user.setRank(UserRank.BEGINNER);
            return user;
        });

        User updatedUser = streakService.applySuccessfulLogin(user);

        assertEquals(1, updatedUser.getStreak());
        assertEquals(120, updatedUser.getXp());
        assertEquals(Instant.parse("2026-06-02T10:15:30Z"), updatedUser.getLastLogin());
        verify(xpService).addXpAndRecalculateRank(user, 30);
    }

    @Test
    void applySuccessfulLogin_shouldRecalculateRankWhenDailyXpCrossesThreshold() {
        User user = createUser();
        user.setXp(490);
        user.setStreak(1);
        user.setLastLogin(Instant.parse("2026-06-01T07:00:00Z"));
        user.setRank(UserRank.BEGINNER);
        when(xpService.addXpAndRecalculateRank(user, 30)).thenAnswer(invocation -> {
            user.setXp(520);
            user.setRank(UserRank.CODER);
            return user;
        });

        User updatedUser = streakService.applySuccessfulLogin(user);

        assertEquals(2, updatedUser.getStreak());
        assertEquals(520, updatedUser.getXp());
        assertEquals(UserRank.CODER, updatedUser.getRank());
    }

    @Test
    void applySuccessfulLogin_shouldHandleInvalidStreakSafelyOnSameDay() {
        User user = createUser();
        user.setXp(30);
        user.setStreak(null);
        user.setLastLogin(Instant.parse("2026-06-02T07:00:00Z"));
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = streakService.applySuccessfulLogin(user);

        assertSame(user, updatedUser);
        assertEquals(1, updatedUser.getStreak());
        assertEquals(30, updatedUser.getXp());
        verify(userRepository).save(user);
        verify(xpService, never()).addXpAndRecalculateRank(user, 30);
    }

    private User createUser() {
        Instant now = Instant.parse("2026-05-31T09:00:00Z");
        User user = new User(UUID.randomUUID(), "Streak User", "streak@example.com", "hashed-password");
        user.setRank(UserRank.BEGINNER);
        user.setRole(UserRole.STUDENT);
        user.setXp(0);
        user.setStreak(0);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }
}
