package com.codequest.problem.dto;

import java.util.UUID;

public record SubmitCodeResponse(
        UUID problemId,
        String language,
        String stdout,
        String stderr,
        String output,
        Integer exitCode,
        Integer runtimeMs,
        boolean passed,
        int xpAwarded,
        boolean firstAccepted,
        String message
) {
}
