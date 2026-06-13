package com.codequest.problem.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CodingProblemResponse(
        UUID problemId,
        String title,
        String description,
        String difficulty,
        int xpReward,
        Map<String, String> starterCode,
        List<Map<String, String>> sampleTestCases
) {
}
