package com.codequest.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.codequest.course.CourseDifficulty;

class GeminiServiceTest {

    @Test
    void shouldBeInstantiableWithoutNetworkCallsAndDelegatePromptBuilding() {
        GeminiProperties properties = new GeminiProperties();
        properties.setApiKey("test-api-key");
        properties.setModel("test-model");
        properties.setBaseUrl("https://example.test/gemini");

        PromptBuilder promptBuilder = new PromptBuilder();
        GeminiService geminiService = new GeminiService(properties, promptBuilder);

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

        GeminiService geminiService = new GeminiService(properties, promptBuilder);

        assertFalse(geminiService.isConfigured());
    }
}
