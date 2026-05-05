package com.codequest.ai;

public interface GeminiClient {

    String generateContent(String baseUrl, String model, String apiKey, String prompt);
}
