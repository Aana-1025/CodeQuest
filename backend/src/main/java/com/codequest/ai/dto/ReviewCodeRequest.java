package com.codequest.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewCodeRequest(
        @NotBlank
        String language,
        @NotBlank
        @Size(max = 20000)
        String code,
        @Size(max = 200)
        String problemTitle,
        @Size(max = 2000)
        String problemDescription
) {
}
