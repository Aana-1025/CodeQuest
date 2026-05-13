package com.codequest.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SubmitQuizAnswerRequest(
        @NotBlank
        @Pattern(regexp = "(?i)^\\s*[ABCD]\\s*$")
        String selectedAnswer
) {
}
