package com.codequest.note.dto;

import java.time.Instant;
import java.util.UUID;

public record NoteResponse(
        UUID noteId,
        UUID levelId,
        String content,
        Instant createdAt,
        Instant updatedAt
) {
}
