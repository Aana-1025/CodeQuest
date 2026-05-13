package com.codequest.course.dto;

import java.util.List;
import java.util.UUID;

import com.codequest.course.CourseDifficulty;
import com.codequest.course.CourseSourceType;

public record CourseResponse(
        UUID courseId,
        String title,
        String description,
        CourseDifficulty difficulty,
        CourseSourceType sourceType,
        int totalXp,
        List<CourseLevelResponse> levels
) {
}
