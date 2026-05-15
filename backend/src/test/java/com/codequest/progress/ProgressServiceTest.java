package com.codequest.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.level.Level;
import com.codequest.level.LevelRepository;
import com.codequest.progress.dto.LevelCompletionResponse;
import com.codequest.user.User;
import com.codequest.user.UserRank;
import com.codequest.user.UserRepository;
import com.codequest.user.UserRole;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private ProgressRepository progressRepository;

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private UserRepository userRepository;

    private ProgressService progressService;

    @BeforeEach
    void setUp() {
        progressService = new ProgressService(progressRepository, levelRepository, userRepository);
    }

    @Test
    void completeLevel_shouldCreateProgressAndAwardLevelXpOnFirstCompletion() {
        User user = createUser("progress-owner@example.com");
        user.setXp(10);
        Level level = createLevel(50);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(progressRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        ArgumentCaptor<Progress> progressCaptor = ArgumentCaptor.forClass(Progress.class);
        when(progressRepository.save(progressCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        LevelCompletionResponse response = progressService.completeLevel(user.getId(), level.getId());

        assertEquals(level.getId(), response.levelId());
        assertTrue(response.completed());
        assertFalse(response.alreadyCompleted());
        assertEquals(50, response.xpAwarded());
        assertEquals(60, response.totalXp());
        assertNotNull(response.completedAt());
        assertEquals(60, user.getXp());

        Progress savedProgress = progressCaptor.getValue();
        assertEquals(user, savedProgress.getUser());
        assertEquals(level, savedProgress.getLevel());
        assertTrue(savedProgress.isCompleted());
        assertNotNull(savedProgress.getCompletedAt());
    }

    @Test
    void completeLevel_shouldBeIdempotentForRepeatedCompletion() {
        User user = createUser("progress-repeat@example.com");
        user.setXp(50);
        Level level = createLevel(50);
        Instant completedAt = Instant.parse("2026-05-15T12:00:00Z");
        Progress existingProgress = new Progress(
                UUID.randomUUID(),
                user,
                level,
                true,
                null,
                completedAt,
                completedAt.minusSeconds(30),
                completedAt
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(progressRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.of(existingProgress));

        LevelCompletionResponse response = progressService.completeLevel(user.getId(), level.getId());

        assertTrue(response.completed());
        assertTrue(response.alreadyCompleted());
        assertEquals(0, response.xpAwarded());
        assertEquals(50, response.totalXp());
        assertEquals(completedAt, response.completedAt());
        assertEquals(50, user.getXp());
        verify(userRepository, never()).save(any(User.class));
        verify(progressRepository, never()).save(any(Progress.class));
    }

    @Test
    void completeLevel_shouldNotCreateDuplicateProgressForRepeatedCompletion() {
        User user = createUser("progress-no-duplicate@example.com");
        Level level = createLevel(50);
        Progress existingProgress = new Progress(
                UUID.randomUUID(),
                user,
                level,
                true,
                null,
                Instant.now(),
                Instant.now().minusSeconds(10),
                Instant.now()
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(progressRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.of(existingProgress));

        progressService.completeLevel(user.getId(), level.getId());

        verify(progressRepository, never()).save(any(Progress.class));
    }

    @Test
    void completeLevel_shouldAllowDifferentUsersToCompleteSameLevelSeparately() {
        User firstUser = createUser("first-progress@example.com");
        User secondUser = createUser("second-progress@example.com");
        Level level = createLevel(50);

        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.of(firstUser));
        when(userRepository.findById(secondUser.getId())).thenReturn(Optional.of(secondUser));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(progressRepository.findByUserIdAndLevelId(firstUser.getId(), level.getId())).thenReturn(Optional.empty());
        when(progressRepository.findByUserIdAndLevelId(secondUser.getId(), level.getId())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(progressRepository.save(any(Progress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LevelCompletionResponse firstResponse = progressService.completeLevel(firstUser.getId(), level.getId());
        LevelCompletionResponse secondResponse = progressService.completeLevel(secondUser.getId(), level.getId());

        assertEquals(50, firstResponse.xpAwarded());
        assertEquals(50, secondResponse.xpAwarded());
        assertEquals(50, firstUser.getXp());
        assertEquals(50, secondUser.getXp());
        assertNotEquals(firstResponse.completedAt(), null);
        verify(progressRepository, times(2)).save(any(Progress.class));
    }

    @Test
    void completeLevel_shouldThrowNotFoundWhenLevelMissing() {
        User user = createUser("missing-level-progress@example.com");
        UUID missingLevelId = UUID.randomUUID();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(missingLevelId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(
                ApiException.class,
                () -> progressService.completeLevel(user.getId(), missingLevelId)
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        assertEquals("Level not found.", exception.getMessage());
        verify(progressRepository, never()).save(any(Progress.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void completeLevel_shouldTreatNullUserXpAsZero() {
        User user = createUser("null-xp-progress@example.com");
        user.setXp(null);
        Level level = createLevel(50);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(progressRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(progressRepository.save(any(Progress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LevelCompletionResponse response = progressService.completeLevel(user.getId(), level.getId());

        assertEquals(50, response.xpAwarded());
        assertEquals(50, response.totalXp());
        assertEquals(50, user.getXp());
    }

    @Test
    void completeLevel_shouldReturnSafeExpectedFieldsAndValues() {
        User user = createUser("safe-fields-progress@example.com");
        user.setXp(5);
        Level level = createLevel(25);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(progressRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(progressRepository.save(any(Progress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LevelCompletionResponse response = progressService.completeLevel(user.getId(), level.getId());

        assertEquals(level.getId(), response.levelId());
        assertTrue(response.completed());
        assertFalse(response.alreadyCompleted());
        assertEquals(25, response.xpAwarded());
        assertEquals(30, response.totalXp());
        assertNotNull(response.completedAt());
    }

    private User createUser(String email) {
        Instant now = Instant.now();
        User user = new User(UUID.randomUUID(), "Progress User", email, "hashed-password");
        user.setRank(UserRank.BEGINNER);
        user.setRole(UserRole.STUDENT);
        user.setXp(0);
        user.setStreak(0);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }

    private Level createLevel(int xpReward) {
        Instant now = Instant.now();
        return new Level(
                UUID.randomUUID(),
                null,
                "Level Completion Basics",
                "# Level Completion Basics",
                1,
                false,
                xpReward,
                now,
                now
        );
    }
}
