package com.codequest.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
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
class XPServiceTest {

    @Mock
    private UserRepository userRepository;

    private XPService xpService;

    @BeforeEach
    void setUp() {
        xpService = new XPService(userRepository);
    }

    @Test
    void calculateRankForXp_shouldReturnBeginnerAtZero() {
        assertEquals(UserRank.BEGINNER, xpService.calculateRankForXp(0));
    }

    @Test
    void calculateRankForXp_shouldReturnBeginnerBelowCoderThreshold() {
        assertEquals(UserRank.BEGINNER, xpService.calculateRankForXp(499));
    }

    @Test
    void calculateRankForXp_shouldReturnCoderAtThreshold() {
        assertEquals(UserRank.CODER, xpService.calculateRankForXp(500));
    }

    @Test
    void calculateRankForXp_shouldReturnCoderBelowDeveloperThreshold() {
        assertEquals(UserRank.CODER, xpService.calculateRankForXp(1999));
    }

    @Test
    void calculateRankForXp_shouldReturnDeveloperAtThreshold() {
        assertEquals(UserRank.DEVELOPER, xpService.calculateRankForXp(2000));
    }

    @Test
    void calculateRankForXp_shouldReturnEngineerAtThreshold() {
        assertEquals(UserRank.ENGINEER, xpService.calculateRankForXp(5000));
    }

    @Test
    void calculateRankForXp_shouldReturnArchitectAtThreshold() {
        assertEquals(UserRank.ARCHITECT, xpService.calculateRankForXp(12000));
    }

    @Test
    void calculateRankForXp_shouldReturnLegendAtThreshold() {
        assertEquals(UserRank.LEGEND, xpService.calculateRankForXp(25000));
    }

    @Test
    void addXpAndRecalculateRank_shouldUpdateUserXpAndRank() {
        User user = createUser();
        user.setXp(480);
        user.setRank(UserRank.BEGINNER);
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = xpService.addXpAndRecalculateRank(user, 25);

        assertEquals(505, updatedUser.getXp());
        assertEquals(UserRank.CODER, updatedUser.getRank());
        verify(userRepository).save(user);
    }

    @Test
    void addXpAndRecalculateRank_shouldAllowZeroXpAndKeepConsistentRank() {
        User user = createUser();
        user.setXp(2000);
        user.setRank(UserRank.BEGINNER);
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = xpService.addXpAndRecalculateRank(user, 0);

        assertEquals(2000, updatedUser.getXp());
        assertEquals(UserRank.DEVELOPER, updatedUser.getRank());
    }

    @Test
    void addXpAndRecalculateRank_shouldTreatNullXpAsZero() {
        User user = createUser();
        user.setXp(null);
        user.setRank(UserRank.BEGINNER);
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = xpService.addXpAndRecalculateRank(user, 20);

        assertEquals(20, updatedUser.getXp());
        assertEquals(UserRank.BEGINNER, updatedUser.getRank());
    }

    @Test
    void addXpAndRecalculateRank_shouldRejectNegativeXpAddition() {
        User user = createUser();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> xpService.addXpAndRecalculateRank(user, -1)
        );

        assertEquals("XP to add cannot be negative.", exception.getMessage());
    }

    private User createUser() {
        Instant now = Instant.now();
        User user = new User(UUID.randomUUID(), "XP Service User", "xp-service@example.com", "hashed-password");
        user.setRank(UserRank.BEGINNER);
        user.setRole(UserRole.STUDENT);
        user.setXp(0);
        user.setStreak(0);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }
}
