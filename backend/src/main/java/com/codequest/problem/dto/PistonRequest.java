package com.codequest.problem.dto;

import java.util.List;

public record PistonRequest(
        String language,
        String version,
        List<PistonFile> files,
        String stdin
) {
    public record PistonFile(String content) {
    }
}
