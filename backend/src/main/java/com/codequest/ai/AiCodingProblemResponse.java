package com.codequest.ai;

import java.util.List;
import java.util.Map;

public record AiCodingProblemResponse(
        String title,
        String description,
        String difficulty,
        Integer xpReward,
        Map<String, String> starterCode,
        List<Map<String, String>> sampleTestCases,
        List<Map<String, String>> hiddenTests
) {
}
