package com.codequest.ai.dto;

import java.util.List;

public record ReviewCodeResponse(
        String timeComplexity,
        String spaceComplexity,
        List<String> correctnessIssues,
        List<String> improvements,
        String betterApproach,
        String encouragement
) {
}
