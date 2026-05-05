package com.codequest.ai;

public class AiResponseValidationException extends RuntimeException {

    public AiResponseValidationException(String message) {
        super(message);
    }

    public AiResponseValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
