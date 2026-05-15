package com.codequest.quiz.dto;

import java.util.List;

public record QuizAttemptHistoryResponse(
        List<QuizAttemptHistoryItemResponse> attempts
) {
}
