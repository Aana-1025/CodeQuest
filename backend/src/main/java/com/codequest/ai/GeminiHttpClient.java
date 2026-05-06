package com.codequest.ai;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GeminiHttpClient implements GeminiClient {

    private final RestClient restClient;

    public GeminiHttpClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public String generateContent(String baseUrl, String model, String apiKey, String prompt) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/v1beta/models/{model}:generateContent")
                .queryParam("key", apiKey)
                .build(model);

        GenerateContentResponse response;
        try {
            response = restClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new GenerateContentRequest(
                            List.of(new Content(List.of(new Part(prompt)))),
                            new GenerationConfig("application/json")
                    ))
                    .retrieve()
                    .body(GenerateContentResponse.class);
        } catch (RestClientException ex) {
            throw new GeminiException(GeminiException.Category.REQUEST_FAILURE, "Gemini request failed.", ex);
        }

        String responseText = extractText(response);
        if (responseText.isBlank()) {
            throw new GeminiException(
                    GeminiException.Category.EMPTY_RESPONSE_TEXT,
                    "Gemini response did not contain usable text."
            );
        }
        if (!looksLikeJsonObject(responseText)) {
            throw new GeminiException(
                    GeminiException.Category.RESPONSE_EXTRACTION_FAILURE,
                    "Gemini response did not contain a parsable JSON object."
            );
        }

        return responseText;
    }

    static String sanitizeGeneratedText(String text) {
        if (text == null) {
            return "";
        }

        String trimmed = text.trim();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            if (firstNewline >= 0) {
                trimmed = trimmed.substring(firstNewline + 1).trim();
            }

            if (trimmed.endsWith("```")) {
                trimmed = trimmed.substring(0, trimmed.length() - 3).trim();
            }
        }

        int firstBrace = trimmed.indexOf('{');
        int lastBrace = trimmed.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace >= firstBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1).trim();
        }

        return trimmed;
    }

    private boolean looksLikeJsonObject(String text) {
        return text.startsWith("{") && text.endsWith("}");
    }

    private String extractText(GenerateContentResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            return "";
        }

        String joinedText = response.candidates().stream()
                .filter(Objects::nonNull)
                .map(Candidate::content)
                .filter(Objects::nonNull)
                .map(Content::parts)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .map(Part::text)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(text -> !text.isBlank())
                .collect(Collectors.joining("\n"));

        return sanitizeGeneratedText(joinedText);
    }

    private record GenerateContentRequest(
            List<Content> contents,
            GenerationConfig generationConfig
    ) {
    }

    private record GenerationConfig(String responseMimeType) {
    }

    private record GenerateContentResponse(List<Candidate> candidates) {
    }

    private record Candidate(Content content) {
    }

    private record Content(List<Part> parts) {
    }

    private record Part(String text) {
    }
}
