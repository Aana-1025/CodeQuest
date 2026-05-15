package com.codequest.progress.dto;

import java.util.List;
import java.util.UUID;

public record CourseProgressResponse(
        UUID courseId,
        int completedLevels,
        int totalLevels,
        int progressPercent,
        boolean courseCompleted,
        List<LevelProgressResponse> levels
) {
}
