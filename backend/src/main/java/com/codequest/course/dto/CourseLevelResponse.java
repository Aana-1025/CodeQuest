package com.codequest.course.dto;

import java.util.UUID;

public record CourseLevelResponse(
        UUID levelId,
        int orderNumber,
        String title,
        String contentMarkdown,
        int xpReward,
        boolean isBoss
) {
}
