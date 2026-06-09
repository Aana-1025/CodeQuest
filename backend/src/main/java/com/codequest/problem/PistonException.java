package com.codequest.problem;

public class PistonException extends RuntimeException {

    public enum Category {
        REQUEST_FAILURE,
        INVALID_RESPONSE
    }

    private final Category category;

    public PistonException(Category category, String message) {
        super(message);
        this.category = category;
    }

    public PistonException(Category category, String message, Throwable cause) {
        super(message, cause);
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }
}
