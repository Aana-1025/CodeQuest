package com.codequest.ai;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.codequest.ai.dto.ReviewCodeRequest;
import com.codequest.ai.dto.ReviewCodeResponse;
import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;

@Service
public class AiCodeReviewService {

    private static final Set<String> ALLOWED_LANGUAGES = Set.of("java", "python", "javascript", "cpp");

    private final GeminiService geminiService;
    private final ResponseParser responseParser;

    public AiCodeReviewService(GeminiService geminiService, ResponseParser responseParser) {
        this.geminiService = geminiService;
        this.responseParser = responseParser;
    }

    public ReviewCodeResponse reviewCode(ReviewCodeRequest request) {
        String normalizedLanguage = normalizeLanguage(request.language());
        validateLanguage(normalizedLanguage);

        try {
            String rawJson = geminiService.generateCodeReviewJson(
                    normalizedLanguage,
                    request.code(),
                    request.problemTitle(),
                    request.problemDescription()
            );
            return responseParser.parseCodeReviewResponse(rawJson);
        } catch (GeminiException ex) {
            throw mapGeminiException(ex);
        } catch (AiResponseValidationException ex) {
            throw new ApiException(ErrorCode.AI_RESPONSE_INVALID, "AI review response was invalid.");
        }
    }

    private String normalizeLanguage(String language) {
        return language == null ? "" : language.trim().toLowerCase();
    }

    private void validateLanguage(String language) {
        if (!ALLOWED_LANGUAGES.contains(language)) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "Language must be one of: java, python, javascript, cpp.");
        }
    }

    private ApiException mapGeminiException(GeminiException ex) {
        return switch (ex.getCategory()) {
            case CONFIG_MISSING -> new ApiException(
                    ErrorCode.AI_SERVICE_UNAVAILABLE,
                    "AI review service is not configured."
            );
            case REQUEST_FAILURE -> mapRequestFailure(ex);
            case EMPTY_RESPONSE_TEXT, RESPONSE_EXTRACTION_FAILURE, UNEXPECTED_GEMINI_ERROR -> new ApiException(
                    ErrorCode.AI_RESPONSE_INVALID,
                    "AI review response was invalid."
            );
        };
    }

    private ApiException mapRequestFailure(GeminiException ex) {
        if (Integer.valueOf(429).equals(ex.getHttpStatusCode())) {
            return new ApiException(ErrorCode.RATE_LIMITED, "AI review is temporarily rate limited. Please try again later.");
        }

        return new ApiException(
                ErrorCode.AI_SERVICE_UNAVAILABLE,
                "AI review service is currently unavailable. Please try again later."
        );
    }
}
