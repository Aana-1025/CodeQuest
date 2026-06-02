package com.codequest.progress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
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
import com.codequest.course.Course;
import com.codequest.course.CourseDifficulty;
import com.codequest.course.CourseRepository;
import com.codequest.course.CourseSourceType;
import com.codequest.level.Level;
import com.codequest.level.LevelRepository;
import com.codequest.progress.dto.CourseProgressResponse;
import com.codequest.progress.dto.LevelProgressResponse;
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
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private XPService xpService;

    private ProgressService progressService;

    @BeforeEach
    void setUp() {
        progressService = new ProgressService(progressRepository, levelRepository, courseRepository, userRepository, xpService);
    }

    @Test
    void completeLevel_shouldAllowFirstLevelCompletionWithoutPreviousProgress() {
        User user = createUser("progress-owner@example.com");
        user.setXp(10);
        Level level = createLevel(1, false, 50);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(progressRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.empty());
        when(xpService.addXpAndRecalculateRank(user, 50)).thenAnswer(invocation -> {
            user.setXp(60);
            user.setRank(UserRank.BEGINNER);
            return user;
        });

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
        assertEquals(UserRank.BEGINNER, user.getRank());

        Progress savedProgress = progressCaptor.getValue();
        assertEquals(user, savedProgress.getUser());
        assertEquals(level, savedProgress.getLevel());
        assertTrue(savedProgress.isCompleted());
        assertNotNull(savedProgress.getCompletedAt());
    }

    @Test
    void completeLevel_shouldRejectSecondLevelBeforeFirstLevelIsCompleted() {
        User user = createUser("locked-second@example.com");
        Level level = createLevel(2, false, 75);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(progressRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.empty());
        when(levelRepository.countByCourseIdAndOrderNumberLessThan(level.getCourse().getId(), level.getOrderNumber())).thenReturn(1L);
        when(progressRepository.countCompletedLevelsBeforeOrderNumber(user.getId(), level.getCourse().getId(), level.getOrderNumber())).thenReturn(0L);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> progressService.completeLevel(user.getId(), level.getId())
        );

        assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());
        assertEquals("Complete previous levels before unlocking this level.", exception.getMessage());
        assertEquals(0, user.getXp());
        verify(progressRepository, never()).save(any(Progress.class));
        verify(xpService, never()).addXpAndRecalculateRank(any(User.class), anyInt());
    }

    @Test
    void completeLevel_shouldAllowSecondLevelAfterFirstLevelIsCompleted() {
        User user = createUser("unlocked-second@example.com");
        Level level = createLevel(2, false, 75);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(progressRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.empty());
        when(levelRepository.countByCourseIdAndOrderNumberLessThan(level.getCourse().getId(), level.getOrderNumber())).thenReturn(1L);
        when(progressRepository.countCompletedLevelsBeforeOrderNumber(user.getId(), level.getCourse().getId(), level.getOrderNumber())).thenReturn(1L);
        when(xpService.addXpAndRecalculateRank(user, 75)).thenAnswer(invocation -> {
            user.setXp(75);
            user.setRank(UserRank.BEGINNER);
            return user;
        });
        when(progressRepository.save(any(Progress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LevelCompletionResponse response = progressService.completeLevel(user.getId(), level.getId());

        assertTrue(response.completed());
        assertFalse(response.alreadyCompleted());
        assertEquals(75, response.xpAwarded());
        assertEquals(75, response.totalXp());
    }

    @Test
    void completeLevel_shouldRejectBossLevelBeforeAllPreviousLevelsAreCompleted() {
        User user = createUser("locked-boss@example.com");
        Level bossLevel = createLevel(3, true, 100);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(bossLevel.getId())).thenReturn(Optional.of(bossLevel));
        when(progressRepository.findByUserIdAndLevelId(user.getId(), bossLevel.getId())).thenReturn(Optional.empty());
        when(levelRepository.countByCourseIdAndOrderNumberLessThan(bossLevel.getCourse().getId(), bossLevel.getOrderNumber())).thenReturn(2L);
        when(progressRepository.countCompletedLevelsBeforeOrderNumber(user.getId(), bossLevel.getCourse().getId(), bossLevel.getOrderNumber())).thenReturn(1L);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> progressService.completeLevel(user.getId(), bossLevel.getId())
        );

        assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());
        assertEquals("Complete previous levels before unlocking this level.", exception.getMessage());
        verify(progressRepository, never()).save(any(Progress.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void completeLevel_shouldAllowBossLevelAfterAllPreviousLevelsAreCompleted() {
        User user = createUser("unlocked-boss@example.com");
        Level bossLevel = createLevel(3, true, 100);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(bossLevel.getId())).thenReturn(Optional.of(bossLevel));
        when(progressRepository.findByUserIdAndLevelId(user.getId(), bossLevel.getId())).thenReturn(Optional.empty());
        when(levelRepository.countByCourseIdAndOrderNumberLessThan(bossLevel.getCourse().getId(), bossLevel.getOrderNumber())).thenReturn(2L);
        when(progressRepository.countCompletedLevelsBeforeOrderNumber(user.getId(), bossLevel.getCourse().getId(), bossLevel.getOrderNumber())).thenReturn(2L);
        when(xpService.addXpAndRecalculateRank(user, 100)).thenAnswer(invocation -> {
            user.setXp(100);
            user.setRank(UserRank.BEGINNER);
            return user;
        });
        when(progressRepository.save(any(Progress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LevelCompletionResponse response = progressService.completeLevel(user.getId(), bossLevel.getId());

        assertTrue(response.completed());
        assertFalse(response.alreadyCompleted());
        assertEquals(100, response.xpAwarded());
        assertEquals(100, response.totalXp());
    }

    @Test
    void completeLevel_shouldBeIdempotentForRepeatedCompletion() {
        User user = createUser("progress-repeat@example.com");
        user.setXp(50);
        Level level = createLevel(2, false, 50);
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
        verify(xpService, never()).addXpAndRecalculateRank(any(User.class), anyInt());
        verify(progressRepository, never()).save(any(Progress.class));
    }

    @Test
    void completeLevel_shouldNotCreateDuplicateProgressForRepeatedCompletion() {
        User user = createUser("progress-no-duplicate@example.com");
        Level level = createLevel(3, true, 50);
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
        Level level = createLevel(2, false, 50);

        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.of(firstUser));
        when(userRepository.findById(secondUser.getId())).thenReturn(Optional.of(secondUser));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(progressRepository.findByUserIdAndLevelId(firstUser.getId(), level.getId())).thenReturn(Optional.empty());
        when(progressRepository.findByUserIdAndLevelId(secondUser.getId(), level.getId())).thenReturn(Optional.empty());
        when(levelRepository.countByCourseIdAndOrderNumberLessThan(level.getCourse().getId(), level.getOrderNumber())).thenReturn(1L);
        when(progressRepository.countCompletedLevelsBeforeOrderNumber(firstUser.getId(), level.getCourse().getId(), level.getOrderNumber())).thenReturn(1L);
        when(progressRepository.countCompletedLevelsBeforeOrderNumber(secondUser.getId(), level.getCourse().getId(), level.getOrderNumber())).thenReturn(1L);
        when(xpService.addXpAndRecalculateRank(firstUser, 50)).thenAnswer(invocation -> {
            firstUser.setXp(50);
            firstUser.setRank(UserRank.BEGINNER);
            return firstUser;
        });
        when(xpService.addXpAndRecalculateRank(secondUser, 50)).thenAnswer(invocation -> {
            secondUser.setXp(50);
            secondUser.setRank(UserRank.BEGINNER);
            return secondUser;
        });
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
    void completeLevel_shouldKeepUnlockStateIndependentPerUser() {
        User firstUser = createUser("unlock-owner@example.com");
        User secondUser = createUser("unlock-blocked@example.com");
        Level secondLevel = createLevel(2, false, 75);

        when(userRepository.findById(firstUser.getId())).thenReturn(Optional.of(firstUser));
        when(userRepository.findById(secondUser.getId())).thenReturn(Optional.of(secondUser));
        when(levelRepository.findById(secondLevel.getId())).thenReturn(Optional.of(secondLevel));
        when(progressRepository.findByUserIdAndLevelId(firstUser.getId(), secondLevel.getId())).thenReturn(Optional.empty());
        when(progressRepository.findByUserIdAndLevelId(secondUser.getId(), secondLevel.getId())).thenReturn(Optional.empty());
        when(levelRepository.countByCourseIdAndOrderNumberLessThan(secondLevel.getCourse().getId(), secondLevel.getOrderNumber())).thenReturn(1L);
        when(progressRepository.countCompletedLevelsBeforeOrderNumber(firstUser.getId(), secondLevel.getCourse().getId(), secondLevel.getOrderNumber())).thenReturn(1L);
        when(progressRepository.countCompletedLevelsBeforeOrderNumber(secondUser.getId(), secondLevel.getCourse().getId(), secondLevel.getOrderNumber())).thenReturn(0L);
        when(xpService.addXpAndRecalculateRank(firstUser, 75)).thenAnswer(invocation -> {
            firstUser.setXp(75);
            firstUser.setRank(UserRank.BEGINNER);
            return firstUser;
        });
        when(progressRepository.save(any(Progress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LevelCompletionResponse firstUserResponse = progressService.completeLevel(firstUser.getId(), secondLevel.getId());

        ApiException exception = assertThrows(
                ApiException.class,
                () -> progressService.completeLevel(secondUser.getId(), secondLevel.getId())
        );

        assertEquals(75, firstUserResponse.xpAwarded());
        assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());
        assertEquals("Complete previous levels before unlocking this level.", exception.getMessage());
        assertEquals(75, firstUser.getXp());
        assertEquals(0, secondUser.getXp());
        verify(progressRepository, times(1)).save(any(Progress.class));
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
        verify(xpService, never()).addXpAndRecalculateRank(any(User.class), anyInt());
    }

    @Test
    void completeLevel_shouldTreatNullUserXpAsZero() {
        User user = createUser("null-xp-progress@example.com");
        user.setXp(null);
        Level level = createLevel(1, false, 50);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(progressRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.empty());
        when(xpService.addXpAndRecalculateRank(user, 50)).thenAnswer(invocation -> {
            user.setXp(50);
            user.setRank(UserRank.BEGINNER);
            return user;
        });
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
        Level level = createLevel(1, false, 25);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(progressRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.empty());
        when(xpService.addXpAndRecalculateRank(user, 25)).thenAnswer(invocation -> {
            user.setXp(30);
            user.setRank(UserRank.BEGINNER);
            return user;
        });
        when(progressRepository.save(any(Progress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LevelCompletionResponse response = progressService.completeLevel(user.getId(), level.getId());

        assertEquals(level.getId(), response.levelId());
        assertTrue(response.completed());
        assertFalse(response.alreadyCompleted());
        assertEquals(25, response.xpAwarded());
        assertEquals(30, response.totalXp());
        assertNotNull(response.completedAt());
    }

    @Test
    void completeLevel_shouldRecalculateRankWhenUserCrossesCoderThreshold() {
        User user = createUser("rank-threshold-progress@example.com");
        user.setXp(450);
        user.setRank(UserRank.BEGINNER);
        Level level = createLevel(1, false, 50);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(levelRepository.findById(level.getId())).thenReturn(Optional.of(level));
        when(progressRepository.findByUserIdAndLevelId(user.getId(), level.getId())).thenReturn(Optional.empty());
        when(xpService.addXpAndRecalculateRank(user, 50)).thenAnswer(invocation -> {
            user.setXp(500);
            user.setRank(UserRank.CODER);
            return user;
        });
        when(progressRepository.save(any(Progress.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LevelCompletionResponse response = progressService.completeLevel(user.getId(), level.getId());

        assertEquals(50, response.xpAwarded());
        assertEquals(500, response.totalXp());
        assertEquals(500, user.getXp());
        assertEquals(UserRank.CODER, user.getRank());
    }

    @Test
    void getCourseProgress_shouldReturnEmptyProgressForNewUserCourse() {
        User user = createUser("empty-progress@example.com");
        Course course = createCourse();
        Level firstLevel = createLevel(course, 1, false, 50, "First Level");
        Level secondLevel = createLevel(course, 2, false, 75, "Second Level");
        Level bossLevel = createLevel(course, 3, true, 100, "Boss Level");

        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(levelRepository.findByCourseIdOrderByOrderNumberAsc(course.getId())).thenReturn(List.of(firstLevel, secondLevel, bossLevel));
        when(progressRepository.findByUserIdAndLevelCourseIdAndCompletedTrue(user.getId(), course.getId())).thenReturn(List.of());

        CourseProgressResponse response = progressService.getCourseProgress(user.getId(), course.getId());

        assertEquals(course.getId(), response.courseId());
        assertEquals(0, response.completedLevels());
        assertEquals(3, response.totalLevels());
        assertEquals(0, response.progressPercent());
        assertFalse(response.courseCompleted());
        assertLevelProgress(response.levels().get(0), firstLevel, false, true, null);
        assertLevelProgress(response.levels().get(1), secondLevel, false, false, null);
        assertLevelProgress(response.levels().get(2), bossLevel, false, false, null);
    }

    @Test
    void getCourseProgress_shouldReturnUpdatedStateAfterCompletingLevelOne() {
        User user = createUser("one-level-progress@example.com");
        Course course = createCourse();
        Level firstLevel = createLevel(course, 1, false, 50, "First Level");
        Level secondLevel = createLevel(course, 2, false, 75, "Second Level");
        Level bossLevel = createLevel(course, 3, true, 100, "Boss Level");
        Instant completedAt = Instant.parse("2026-05-16T10:15:30Z");
        Progress firstProgress = createCompletedProgress(user, firstLevel, completedAt);

        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(levelRepository.findByCourseIdOrderByOrderNumberAsc(course.getId())).thenReturn(List.of(firstLevel, secondLevel, bossLevel));
        when(progressRepository.findByUserIdAndLevelCourseIdAndCompletedTrue(user.getId(), course.getId())).thenReturn(List.of(firstProgress));

        CourseProgressResponse response = progressService.getCourseProgress(user.getId(), course.getId());

        assertEquals(1, response.completedLevels());
        assertEquals(3, response.totalLevels());
        assertEquals(33, response.progressPercent());
        assertFalse(response.courseCompleted());
        assertLevelProgress(response.levels().get(0), firstLevel, true, true, completedAt);
        assertLevelProgress(response.levels().get(1), secondLevel, false, true, null);
        assertLevelProgress(response.levels().get(2), bossLevel, false, false, null);
    }

    @Test
    void getCourseProgress_shouldReturnCompletedCourseStateAfterAllLevelsCompleted() {
        User user = createUser("all-levels-progress@example.com");
        Course course = createCourse();
        Level firstLevel = createLevel(course, 1, false, 50, "First Level");
        Level secondLevel = createLevel(course, 2, false, 75, "Second Level");
        Level bossLevel = createLevel(course, 3, true, 100, "Boss Level");

        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(levelRepository.findByCourseIdOrderByOrderNumberAsc(course.getId())).thenReturn(List.of(firstLevel, secondLevel, bossLevel));
        when(progressRepository.findByUserIdAndLevelCourseIdAndCompletedTrue(user.getId(), course.getId())).thenReturn(List.of(
                createCompletedProgress(user, firstLevel, Instant.parse("2026-05-16T10:00:00Z")),
                createCompletedProgress(user, secondLevel, Instant.parse("2026-05-16T10:10:00Z")),
                createCompletedProgress(user, bossLevel, Instant.parse("2026-05-16T10:20:00Z"))
        ));

        CourseProgressResponse response = progressService.getCourseProgress(user.getId(), course.getId());

        assertEquals(3, response.completedLevels());
        assertEquals(3, response.totalLevels());
        assertEquals(100, response.progressPercent());
        assertTrue(response.courseCompleted());
        assertTrue(response.levels().stream().allMatch(LevelProgressResponse::completed));
        assertTrue(response.levels().stream().allMatch(LevelProgressResponse::unlocked));
        assertTrue(response.levels().stream().allMatch(level -> level.completedAt() != null));
    }

    @Test
    void getCourseProgress_shouldKeepProgressIndependentPerUser() {
        User firstUser = createUser("first-course-progress@example.com");
        User secondUser = createUser("second-course-progress@example.com");
        Course course = createCourse();
        Level firstLevel = createLevel(course, 1, false, 50, "First Level");
        Level secondLevel = createLevel(course, 2, false, 75, "Second Level");
        Level bossLevel = createLevel(course, 3, true, 100, "Boss Level");

        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(levelRepository.findByCourseIdOrderByOrderNumberAsc(course.getId())).thenReturn(List.of(firstLevel, secondLevel, bossLevel));
        when(progressRepository.findByUserIdAndLevelCourseIdAndCompletedTrue(firstUser.getId(), course.getId())).thenReturn(List.of(
                createCompletedProgress(firstUser, firstLevel, Instant.parse("2026-05-16T11:00:00Z"))
        ));
        when(progressRepository.findByUserIdAndLevelCourseIdAndCompletedTrue(secondUser.getId(), course.getId())).thenReturn(List.of());

        CourseProgressResponse firstResponse = progressService.getCourseProgress(firstUser.getId(), course.getId());
        CourseProgressResponse secondResponse = progressService.getCourseProgress(secondUser.getId(), course.getId());

        assertEquals(1, firstResponse.completedLevels());
        assertTrue(firstResponse.levels().get(1).unlocked());
        assertEquals(0, secondResponse.completedLevels());
        assertFalse(secondResponse.levels().get(1).unlocked());
        assertFalse(secondResponse.levels().get(2).unlocked());
        assertTrue(secondResponse.levels().stream().allMatch(level -> !level.completed()));
        assertTrue(secondResponse.levels().stream().allMatch(level -> level.completedAt() == null));
    }

    @Test
    void getCourseProgress_shouldThrowNotFoundWhenCourseMissing() {
        User user = createUser("missing-course-progress@example.com");
        UUID missingCourseId = UUID.randomUUID();

        when(courseRepository.findById(missingCourseId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(
                ApiException.class,
                () -> progressService.getCourseProgress(user.getId(), missingCourseId)
        );

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        assertEquals("Course not found.", exception.getMessage());
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

    private Course createCourse() {
        Instant now = Instant.now();
        return new Course(
                UUID.randomUUID(),
                "progress-course-" + UUID.randomUUID(),
                "Progress Course",
                "Progress Course Description",
                createUser("course-owner-" + UUID.randomUUID() + "@example.com"),
                CourseDifficulty.BEGINNER,
                false,
                225,
                CourseSourceType.PLACEHOLDER,
                now,
                now
        );
    }

    private Level createLevel(int orderNumber, boolean isBoss, int xpReward) {
        return createLevel(createCourse(), orderNumber, isBoss, xpReward, "Level Completion Basics");
    }

    private Level createLevel(Course course, int orderNumber, boolean isBoss, int xpReward, String title) {
        Instant now = Instant.now();
        return new Level(
                UUID.randomUUID(),
                course,
                title,
                "# Level Completion Basics",
                orderNumber,
                isBoss,
                xpReward,
                now,
                now
        );
    }

    private Progress createCompletedProgress(User user, Level level, Instant completedAt) {
        return new Progress(
                UUID.randomUUID(),
                user,
                level,
                true,
                null,
                completedAt,
                completedAt.minusSeconds(60),
                completedAt
        );
    }

    private void assertLevelProgress(
            LevelProgressResponse response,
            Level level,
            boolean completed,
            boolean unlocked,
            Instant completedAt
    ) {
        assertEquals(level.getId(), response.levelId());
        assertEquals(level.getOrderNumber(), response.orderNumber());
        assertEquals(level.getTitle(), response.title());
        assertEquals(level.isBoss(), response.isBoss());
        assertEquals(level.getXpReward(), response.xpReward());
        assertEquals(completed, response.completed());
        assertEquals(unlocked, response.unlocked());
        assertEquals(completedAt, response.completedAt());
    }
}
