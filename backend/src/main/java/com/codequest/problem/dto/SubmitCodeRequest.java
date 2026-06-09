package com.codequest.problem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmitCodeRequest(
        @NotBlank
        String language,
        @NotBlank
        @Size(max = 20000)
        String code,
        @Size(max = 5000)
        String stdin,
        @NotBlank
        @Size(max = 5000)
        String expectedOutput
) {
}
