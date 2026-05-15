package com.codequest.progress;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.level.Level;
import com.codequest.level.LevelRepository;
import com.codequest.progress.dto.LevelCompletionResponse;
import com.codequest.user.User;
import com.codequest.user.UserRepository;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final LevelRepository levelRepository;
    private final UserRepository userRepository;

    public ProgressService(ProgressRepository progressRepository, LevelRepository levelRepository, UserRepository userRepository) {
        this.progressRepository = progressRepository;
        this.levelRepository = levelRepository;
        this.userRepository = userRepository;
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

        Instant now = Instant.now();
        int xpAwarded = level.getXpReward() == null ? 0 : level.getXpReward();
        int updatedTotalXp = currentXp(user) + xpAwarded;

        user.setXp(updatedTotalXp);
        userRepository.save(user);

        Progress progress = existingProgress == null
                ? createNewProgress(user, level, now)
                : markProgressCompleted(existingProgress, now);
        progressRepository.save(progress);

        return new LevelCompletionResponse(
                level.getId(),
                true,
                false,
                xpAwarded,
                updatedTotalXp,
                progress.getCompletedAt()
        );
    }

    private int currentXp(User user) {
        return user.getXp() == null ? 0 : user.getXp();
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
