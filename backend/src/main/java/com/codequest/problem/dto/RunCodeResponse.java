package com.codequest.problem.dto;

import java.util.UUID;

public record RunCodeResponse(
        UUID problemId,
        String language,
        String stdout,
        String stderr,
        String output,
        Integer exitCode,
        Integer runtimeMs,
        Boolean passed,
        String message
) {
}
