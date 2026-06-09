package com.codequest.ai;

import com.codequest.course.CourseDifficulty;
import com.codequest.course.dto.GenerateCourseRequest;

import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final GeminiProperties geminiProperties;
    private final PromptBuilder promptBuilder;
    private final GeminiClient geminiClient;

    public GeminiService(GeminiProperties geminiProperties, PromptBuilder promptBuilder, GeminiClient geminiClient) {
        this.geminiProperties = geminiProperties;
        this.promptBuilder = promptBuilder;
        this.geminiClient = geminiClient;
    }

    public boolean isConfigured() {
        return hasText(geminiProperties.getApiKey())
                && hasText(geminiProperties.getModel())
                && hasText(geminiProperties.getBaseUrl());
    }

    public String buildCourseGenerationPrompt(String topic, CourseDifficulty difficulty, String goal) {
        return promptBuilder.buildCourseGenerationPrompt(topic, difficulty, goal);
    }

    public String generateCourseJson(GenerateCourseRequest request) {
        if (!isConfigured()) {
            throw new GeminiException(GeminiException.Category.CONFIG_MISSING, "Gemini is not configured.");
        }

        String prompt = promptBuilder.buildCourseGenerationPrompt(
                request.topic(),
                request.difficulty(),
                request.goal()
        );

        return geminiClient.generateContent(
                geminiProperties.getBaseUrl(),
                geminiProperties.getModel(),
                geminiProperties.getApiKey(),
                prompt
        );
    }

    public String generateCodeReviewJson(String language, String code, String problemTitle, String problemDescription) {
        if (!isConfigured()) {
            throw new GeminiException(GeminiException.Category.CONFIG_MISSING, "Gemini is not configured.");
        }

        String prompt = promptBuilder.buildCodeReviewPrompt(language, code, problemTitle, problemDescription);

        return geminiClient.generateContent(
                geminiProperties.getBaseUrl(),
                geminiProperties.getModel(),
                geminiProperties.getApiKey(),
                prompt
        );
    }

    public String getConfiguredModel() {
        return geminiProperties.getModel();
    }

    public String getConfiguredBaseUrl() {
        return geminiProperties.getBaseUrl();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
