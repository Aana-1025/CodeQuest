package com.codequest.problem.dto;

import java.util.List;
import java.util.UUID;

public record CodeSubmissionHistoryResponse(
        UUID problemId,
        int page,
        int size,
        long totalItems,
        int totalPages,
        List<CodeSubmissionHistoryItemResponse> items
) {
}
