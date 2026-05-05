package com.codequest.course.dto;

import java.util.UUID;

public record CourseLevelSummaryResponse(
        UUID levelId,
        String title,
        Integer orderNumber,
        boolean isBoss,
        Integer xpReward
) {
}
