package com.codequest.course.dto;

import com.codequest.course.CourseDifficulty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GenerateCourseRequest(
        @NotBlank
        @Size(min = 2, max = 80)
        String topic,

        @NotNull
        CourseDifficulty difficulty,

        @Size(max = 200)
        String goal
) {
}
