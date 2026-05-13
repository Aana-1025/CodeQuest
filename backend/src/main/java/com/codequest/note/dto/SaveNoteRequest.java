package com.codequest.note.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SaveNoteRequest(
        @NotNull UUID levelId,
        @NotBlank @Size(max = 5000) String content
) {
}
