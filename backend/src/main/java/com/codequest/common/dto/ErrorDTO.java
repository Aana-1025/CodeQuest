package com.codequest.common.dto;

import java.time.Instant;

public record ErrorDTO(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        String requestId
) {
}