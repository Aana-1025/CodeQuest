package com.codequest.ai;

import com.codequest.course.CourseDifficulty;

import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final GeminiProperties geminiProperties;
    private final PromptBuilder promptBuilder;

    public GeminiService(GeminiProperties geminiProperties, PromptBuilder promptBuilder) {
        this.geminiProperties = geminiProperties;
        this.promptBuilder = promptBuilder;
    }

    public boolean isConfigured() {
        return hasText(geminiProperties.getApiKey())
                && hasText(geminiProperties.getModel())
                && hasText(geminiProperties.getBaseUrl());
    }

    public String buildCourseGenerationPrompt(String topic, CourseDifficulty difficulty, String goal) {
        return promptBuilder.buildCourseGenerationPrompt(topic, difficulty, goal);
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
