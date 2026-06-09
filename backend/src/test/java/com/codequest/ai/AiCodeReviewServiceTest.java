package com.codequest.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codequest.ai.dto.ReviewCodeRequest;
import com.codequest.ai.dto.ReviewCodeResponse;
import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AiCodeReviewServiceTest {

    @Mock
    private GeminiService geminiService;

    @Mock
    private ResponseParser responseParser;

    private AiCodeReviewService aiCodeReviewService;

    @BeforeEach
    void setUp() {
        aiCodeReviewService = new AiCodeReviewService(geminiService, responseParser);
    }

    @Test
    void shouldReturnSafeReviewResponseForValidRequest() {
        ReviewCodeRequest request = new ReviewCodeRequest(
                "java",
                "public class Main {}",
                "Binary Search",
                "Find a target index."
        );
        ReviewCodeResponse expectedResponse = new ReviewCodeResponse(
                "O(log n)",
                "O(1)",
                java.util.List.of(),
                java.util.List.of("Handle overflow in mid calculation."),
                "Binary search is already the right high-level approach here.",
                "Good job choosing an efficient strategy."
        );

        when(geminiService.generateCodeReviewJson("java", "public class Main {}", "Binary Search", "Find a target index."))
                .thenReturn("{\"timeComplexity\":\"O(log n)\"}");
        when(responseParser.parseCodeReviewResponse("{\"timeComplexity\":\"O(log n)\"}")).thenReturn(expectedResponse);

        ReviewCodeResponse response = aiCodeReviewService.reviewCode(request);

        assertEquals(expectedResponse, response);
        verify(geminiService).generateCodeReviewJson("java", "public class Main {}", "Binary Search", "Find a target index.");
        verify(responseParser).parseCodeReviewResponse("{\"timeComplexity\":\"O(log n)\"}");
    }

    @Test
    void shouldRejectInvalidLanguageSafely() {
        ReviewCodeRequest request = new ReviewCodeRequest("ruby", "puts 'hi'", null, null);

        ApiException exception = assertThrows(ApiException.class, () -> aiCodeReviewService.reviewCode(request));

        assertEquals(ErrorCode.BAD_REQUEST, exception.getErrorCode());
        assertEquals("Language must be one of: java, python, javascript, cpp.", exception.getMessage());
        verifyNoMoreInteractions(geminiService, responseParser);
    }

    @Test
    void shouldMapGeminiUnavailableToSafeServiceUnavailable() {
        ReviewCodeRequest request = new ReviewCodeRequest("java", "public class Main {}", null, null);
        when(geminiService.generateCodeReviewJson("java", "public class Main {}", null, null))
                .thenThrow(new GeminiException(GeminiException.Category.REQUEST_FAILURE, "Gemini request failed."));

        ApiException exception = assertThrows(ApiException.class, () -> aiCodeReviewService.reviewCode(request));

        assertEquals(ErrorCode.AI_SERVICE_UNAVAILABLE, exception.getErrorCode());
        assertEquals("AI review service is currently unavailable. Please try again later.", exception.getMessage());
    }

    @Test
    void shouldMapGeminiRateLimitToSafeRateLimitedError() {
        ReviewCodeRequest request = new ReviewCodeRequest("java", "public class Main {}", null, null);
        when(geminiService.generateCodeReviewJson("java", "public class Main {}", null, null))
                .thenThrow(new GeminiException(
                        GeminiException.Category.REQUEST_FAILURE,
                        "Gemini request failed with HTTP status 429 (4xx).",
                        429,
                        null
                ));

        ApiException exception = assertThrows(ApiException.class, () -> aiCodeReviewService.reviewCode(request));

        assertEquals(ErrorCode.RATE_LIMITED, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("rate limited"));
    }

    @Test
    void shouldMapMalformedGeminiJsonToSafeBadGatewayError() {
        ReviewCodeRequest request = new ReviewCodeRequest("java", "public class Main {}", null, null);
        when(geminiService.generateCodeReviewJson("java", "public class Main {}", null, null))
                .thenReturn("{invalid");
        when(responseParser.parseCodeReviewResponse("{invalid"))
                .thenThrow(new AiResponseValidationException("Malformed AI JSON response."));

        ApiException exception = assertThrows(ApiException.class, () -> aiCodeReviewService.reviewCode(request));

        assertEquals(ErrorCode.AI_RESPONSE_INVALID, exception.getErrorCode());
        assertEquals("AI review response was invalid.", exception.getMessage());
    }

    @Test
    void shouldMapMissingGeminiConfigToSafeServiceUnavailableError() {
        ReviewCodeRequest request = new ReviewCodeRequest("java", "public class Main {}", null, null);
        when(geminiService.generateCodeReviewJson("java", "public class Main {}", null, null))
                .thenThrow(new GeminiException(GeminiException.Category.CONFIG_MISSING, "Gemini is not configured."));

        ApiException exception = assertThrows(ApiException.class, () -> aiCodeReviewService.reviewCode(request));

        assertEquals(ErrorCode.AI_SERVICE_UNAVAILABLE, exception.getErrorCode());
        assertEquals("AI review service is not configured.", exception.getMessage());
    }
}
