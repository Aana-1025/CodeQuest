package com.codequest.problem.dto;

import java.time.Instant;
import java.util.UUID;

public record CodeSubmissionHistoryItemResponse(
        UUID submissionId,
        UUID problemId,
        String language,
        String code,
        boolean passed,
        Integer passedTestCases,
        Integer totalTestCases,
        Integer runtimeMs,
        Integer memoryKb,
        String aiReview,
        Instant submittedAt
) {
}
