package com.codequest.flashcard.dto;

import java.util.UUID;

public record FlashcardResponse(
        UUID flashcardId,
        int orderNumber,
        String front,
        String back,
        String conceptTag
) {
}
