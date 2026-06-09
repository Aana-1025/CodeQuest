package com.codequest.problem.dto;

public record PistonResponse(
        String language,
        String version,
        PistonStage compile,
        PistonStage run
) {
    public record PistonStage(
            String stdout,
            String stderr,
            String output,
            Integer code
    ) {
    }
}
