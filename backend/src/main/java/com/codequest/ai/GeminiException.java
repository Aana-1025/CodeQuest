package com.codequest.ai;

public class GeminiException extends RuntimeException {

    private final Category category;

    public GeminiException(String message) {
        this(Category.UNEXPECTED_GEMINI_ERROR, message);
    }

    public GeminiException(String message, Throwable cause) {
        this(Category.UNEXPECTED_GEMINI_ERROR, message, cause);
    }

    public GeminiException(Category category, String message) {
        super(message);
        this.category = category;
    }

    public GeminiException(Category category, String message, Throwable cause) {
        super(message, cause);
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public enum Category {
        CONFIG_MISSING,
        REQUEST_FAILURE,
        EMPTY_RESPONSE_TEXT,
        RESPONSE_EXTRACTION_FAILURE,
        UNEXPECTED_GEMINI_ERROR
    }
}
