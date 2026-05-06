package com.codequest.ai;

public class GeminiException extends RuntimeException {

    private final Category category;
    private final Integer httpStatusCode;
    private final String httpStatusFamily;

    public GeminiException(String message) {
        this(Category.UNEXPECTED_GEMINI_ERROR, message, null, null);
    }

    public GeminiException(String message, Throwable cause) {
        this(Category.UNEXPECTED_GEMINI_ERROR, message, null, cause);
    }

    public GeminiException(Category category, String message) {
        this(category, message, null, null);
    }

    public GeminiException(Category category, String message, Throwable cause) {
        this(category, message, null, cause);
    }

    public GeminiException(Category category, String message, Integer httpStatusCode, Throwable cause) {
        super(message, cause);
        this.category = category;
        this.httpStatusCode = httpStatusCode;
        this.httpStatusFamily = determineHttpStatusFamily(httpStatusCode);
    }

    public Category getCategory() {
        return category;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getHttpStatusFamily() {
        return httpStatusFamily;
    }

    private String determineHttpStatusFamily(Integer statusCode) {
        if (statusCode == null || statusCode < 100) {
            return null;
        }

        return (statusCode / 100) + "xx";
    }

    public enum Category {
        CONFIG_MISSING,
        REQUEST_FAILURE,
        EMPTY_RESPONSE_TEXT,
        RESPONSE_EXTRACTION_FAILURE,
        UNEXPECTED_GEMINI_ERROR
    }
}
