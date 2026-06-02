package com.codequest.progress;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.course.Course;
import com.codequest.course.CourseRepository;
import com.codequest.level.Level;
import com.codequest.level.LevelRepository;
import com.codequest.progress.dto.CourseProgressResponse;
import com.codequest.progress.dto.LevelCompletionResponse;
import com.codequest.progress.dto.LevelProgressResponse;
import com.codequest.user.User;
import com.codequest.user.UserRepository;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final LevelRepository levelRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final XPService xpService;

    public ProgressService(
            ProgressRepository progressRepository,
            LevelRepository levelRepository,
            CourseRepository courseRepository,
            UserRepository userRepository,
            XPService xpService
    ) {
        this.progressRepository = progressRepository;
        this.levelRepository = levelRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.xpService = xpService;
    }

    @Transactional
    public LevelCompletionResponse completeLevel(UUID userId, UUID levelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "User not found."));
        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Level not found."));

        Progress existingProgress = progressRepository.findByUserIdAndLevelId(userId, levelId).orElse(null);
        if (existingProgress != null && existingProgress.isCompleted()) {
            return new LevelCompletionResponse(
                    level.getId(),
                    true,
                    true,
                    0,
                    currentXp(user),
                    existingProgress.getCompletedAt()
            );
        }

        if (!isUnlockedForUser(userId, level)) {
            throw new ApiException(ErrorCode.FORBIDDEN, "Complete previous levels before unlocking this level.");
        }

        Instant now = Instant.now();
        int xpAwarded = level.getXpReward() == null ? 0 : level.getXpReward();
        User updatedUser = xpService.addXpAndRecalculateRank(user, xpAwarded);

        Progress progress = existingProgress == null
                ? createNewProgress(updatedUser, level, now)
                : markProgressCompleted(existingProgress, now);
        progressRepository.save(progress);

        return new LevelCompletionResponse(
                level.getId(),
                true,
                false,
                xpAwarded,
                currentXp(updatedUser),
                progress.getCompletedAt()
        );
    }

    @Transactional(readOnly = true)
    public CourseProgressResponse getCourseProgress(UUID userId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Course not found."));
        List<Level> levels = levelRepository.findByCourseIdOrderByOrderNumberAsc(course.getId());
        List<Progress> completedProgressRows = progressRepository.findByUserIdAndLevelCourseIdAndCompletedTrue(userId, course.getId());

        Map<UUID, Progress> completedProgressByLevelId = new HashMap<>();
        for (Progress progress : completedProgressRows) {
            completedProgressByLevelId.put(progress.getLevel().getId(), progress);
        }

        List<LevelProgressResponse> levelResponses = new java.util.ArrayList<>();
        boolean allPreviousLevelsCompleted = true;

        for (Level level : levels) {
            Progress completedProgress = completedProgressByLevelId.get(level.getId());
            boolean completed = completedProgress != null && completedProgress.isCompleted();
            boolean unlocked = completed || isUnlockedForOrderedCourseLevel(level, allPreviousLevelsCompleted);

            levelResponses.add(new LevelProgressResponse(
                    level.getId(),
                    level.getOrderNumber() == null ? 0 : level.getOrderNumber(),
                    level.getTitle(),
                    level.isBoss(),
                    level.getXpReward() == null ? 0 : level.getXpReward(),
                    completed,
                    unlocked,
                    completed ? completedProgress.getCompletedAt() : null
            ));

            allPreviousLevelsCompleted = allPreviousLevelsCompleted && completed;
        }

        int completedLevels = completedProgressRows.size();
        int totalLevels = levels.size();
        int progressPercent = totalLevels == 0 ? 0 : (completedLevels * 100) / totalLevels;
        boolean courseCompleted = totalLevels > 0 && completedLevels == totalLevels;

        return new CourseProgressResponse(
                course.getId(),
                completedLevels,
                totalLevels,
                progressPercent,
                courseCompleted,
                levelResponses
        );
    }

    private int currentXp(User user) {
        return user.getXp() == null ? 0 : user.getXp();
    }

    private boolean isUnlockedForUser(UUID userId, Level level) {
        if (level.getOrderNumber() == null || level.getOrderNumber() <= 1) {
            return true;
        }

        UUID courseId = level.getCourse().getId();
        long previousLevelCount = levelRepository.countByCourseIdAndOrderNumberLessThan(courseId, level.getOrderNumber());
        long completedPreviousLevelCount = progressRepository.countCompletedLevelsBeforeOrderNumber(
                userId,
                courseId,
                level.getOrderNumber()
        );

        return previousLevelCount == completedPreviousLevelCount;
    }

    private boolean isUnlockedForOrderedCourseLevel(Level level, boolean allPreviousLevelsCompleted) {
        if (level.getOrderNumber() == null || level.getOrderNumber() <= 1) {
            return true;
        }

        return allPreviousLevelsCompleted;
    }

    private Progress createNewProgress(User user, Level level, Instant now) {
        return new Progress(
                UUID.randomUUID(),
                user,
                level,
                true,
                null,
                now,
                now,
                now
        );
    }

    private Progress markProgressCompleted(Progress progress, Instant now) {
        progress.setCompleted(true);
        progress.setCompletedAt(now);
        progress.setUpdatedAt(now);
        return progress;
    }
}
