package com.codequest.course.dto;

import java.util.List;
import java.util.UUID;

public record GenerateCourseResponse(
        UUID courseId,
        String title,
        String description,
        boolean cacheHit,
        List<CourseLevelSummaryResponse> levels
) {
}
