package com.codequest.ai;

import java.util.List;

public record AiCourseResponse(
        String title,
        String description,
        String difficulty,
        List<AiLevelResponse> levels
) {
}
