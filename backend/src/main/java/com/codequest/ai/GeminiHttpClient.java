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
            throw new GeminiException("Gemini request failed.", ex);
        }

        String responseText = extractText(response);
        if (responseText.isBlank()) {
            throw new GeminiException("Gemini response did not contain usable text.");
        }

        return responseText;
    }

    private String extractText(GenerateContentResponse response) {
        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            return "";
        }

        return response.candidates().stream()
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
