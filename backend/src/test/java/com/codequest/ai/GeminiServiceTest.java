package com.codequest.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codequest.course.CourseDifficulty;
import com.codequest.course.dto.GenerateCourseRequest;

@ExtendWith(MockitoExtension.class)
class GeminiServiceTest {

    @Mock
    private GeminiClient geminiClient;

    @Test
    void shouldBeInstantiableWithoutNetworkCallsAndDelegatePromptBuilding() {
        GeminiProperties properties = new GeminiProperties();
        properties.setApiKey("test-api-key");
        properties.setModel("test-model");
        properties.setBaseUrl("https://example.test/gemini");

        PromptBuilder promptBuilder = new PromptBuilder();
        GeminiService geminiService = new GeminiService(properties, promptBuilder, geminiClient);

        String prompt = geminiService.buildCourseGenerationPrompt(
                "Binary Search",
                CourseDifficulty.BEGINNER,
                "DSA interview preparation"
        );

        assertTrue(geminiService.isConfigured());
        assertEquals("test-model", geminiService.getConfiguredModel());
        assertEquals("https://example.test/gemini", geminiService.getConfiguredBaseUrl());
        assertTrue(prompt.contains("Binary Search"));
        assertTrue(prompt.contains("Return JSON only"));
    }

    @Test
    void shouldReportNotConfiguredWhenGeminiSecretsMissing() {
        GeminiProperties properties = new GeminiProperties();
        PromptBuilder promptBuilder = new PromptBuilder();

        GeminiService geminiService = new GeminiService(properties, promptBuilder, geminiClient);

        assertFalse(geminiService.isConfigured());
    }

    @Test
    void shouldGenerateCourseJsonThroughGeminiClientWhenConfigured() {
        GeminiProperties properties = new GeminiProperties();
        properties.setApiKey("test-api-key");
        properties.setModel("test-model");
        properties.setBaseUrl("https://example.test/gemini");

        PromptBuilder promptBuilder = new PromptBuilder();
        GeminiService geminiService = new GeminiService(properties, promptBuilder, geminiClient);
        GenerateCourseRequest request = new GenerateCourseRequest("Binary Search", CourseDifficulty.BEGINNER, "DSA");

        when(geminiClient.generateContent(
                "https://example.test/gemini",
                "test-model",
                "test-api-key",
                promptBuilder.buildCourseGenerationPrompt("Binary Search", CourseDifficulty.BEGINNER, "DSA")
        )).thenReturn("{\"title\":\"Binary Search\"}");

        String response = geminiService.generateCourseJson(request);

        assertEquals("{\"title\":\"Binary Search\"}", response);
        verify(geminiClient).generateContent(
                "https://example.test/gemini",
                "test-model",
                "test-api-key",
                promptBuilder.buildCourseGenerationPrompt("Binary Search", CourseDifficulty.BEGINNER, "DSA")
        );
    }

    @Test
    void shouldRejectCourseGenerationWhenGeminiIsNotConfigured() {
        GeminiProperties properties = new GeminiProperties();
        PromptBuilder promptBuilder = new PromptBuilder();
        GeminiService geminiService = new GeminiService(properties, promptBuilder, geminiClient);

        GeminiException exception = assertThrows(
                GeminiException.class,
                () -> geminiService.generateCourseJson(new GenerateCourseRequest("Binary Search", CourseDifficulty.BEGINNER, null))
        );

        assertEquals(GeminiException.Category.CONFIG_MISSING, exception.getCategory());
    }

    @Test
    void shouldGenerateCodeReviewJsonThroughGeminiClientWhenConfigured() {
        GeminiProperties properties = new GeminiProperties();
        properties.setApiKey("test-api-key");
        properties.setModel("test-model");
        properties.setBaseUrl("https://example.test/gemini");

        PromptBuilder promptBuilder = new PromptBuilder();
        GeminiService geminiService = new GeminiService(properties, promptBuilder, geminiClient);

        when(geminiClient.generateContent(
                "https://example.test/gemini",
                "test-model",
                "test-api-key",
                promptBuilder.buildCodeReviewPrompt("java", "public class Main {}", "Binary Search", "Find target index.")
        )).thenReturn("{\"timeComplexity\":\"O(log n)\",\"spaceComplexity\":\"O(1)\",\"correctnessIssues\":[],\"improvements\":[],\"betterApproach\":\"Keep binary search.\",\"encouragement\":\"Good work.\"}");

        String response = geminiService.generateCodeReviewJson("java", "public class Main {}", "Binary Search", "Find target index.");

        assertTrue(response.contains("\"timeComplexity\":\"O(log n)\""));
        verify(geminiClient).generateContent(
                "https://example.test/gemini",
                "test-model",
                "test-api-key",
                promptBuilder.buildCodeReviewPrompt("java", "public class Main {}", "Binary Search", "Find target index.")
        );
    }

    @Test
    void shouldRejectCodeReviewWhenGeminiIsNotConfigured() {
        GeminiProperties properties = new GeminiProperties();
        PromptBuilder promptBuilder = new PromptBuilder();
        GeminiService geminiService = new GeminiService(properties, promptBuilder, geminiClient);

        GeminiException exception = assertThrows(
                GeminiException.class,
                () -> geminiService.generateCodeReviewJson("java", "public class Main {}", null, null)
        );

        assertEquals(GeminiException.Category.CONFIG_MISSING, exception.getCategory());
    }

    @Test
    void shouldSanitizeFencedGeminiJsonOutput() {
        String sanitized = GeminiHttpClient.sanitizeGeneratedText("""
                ```json
                {
                  "title": "Binary Search"
                }
                ```
                """);

        assertEquals("""
                {
                  "title": "Binary Search"
                }
                """.trim(), sanitized);
    }

    @Test
    void shouldExtractJsonObjectFromGeminiTextWithExtraProse() {
        String sanitized = GeminiHttpClient.sanitizeGeneratedText("""
                Here is the JSON you requested:
                {
                  "title": "Binary Search"
                }
                """);

        assertEquals("""
                {
                  "title": "Binary Search"
                }
                """.trim(), sanitized);
    }
}
