package com.codequest.course.dto;

import java.util.List;
import java.util.UUID;

import com.codequest.course.CourseSourceType;

public record GenerateCourseResponse(
        UUID courseId,
        String title,
        String description,
        CourseSourceType sourceType,
        boolean cacheHit,
        List<CourseLevelSummaryResponse> levels
) {
}
