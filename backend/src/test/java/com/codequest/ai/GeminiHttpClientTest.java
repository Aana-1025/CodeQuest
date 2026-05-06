package com.codequest.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class GeminiHttpClientTest {

    @Test
    void shouldBuildGenerateContentUriWithoutDuplicatingV1betaSegment() {
        URI uri = GeminiHttpClient.buildGenerateContentUri(
                "https://generativelanguage.googleapis.com/v1beta",
                "gemini-1.5-flash",
                "test-api-key"
        );

        assertEquals("https", uri.getScheme());
        assertEquals("generativelanguage.googleapis.com", uri.getHost());
        assertEquals("/v1beta/models/gemini-1.5-flash:generateContent", uri.getPath());
        assertEquals("key=test-api-key", uri.getQuery());
    }

    @Test
    void shouldDefaultToV1betaWhenBaseUrlDoesNotIncludeApiVersion() {
        URI uri = GeminiHttpClient.buildGenerateContentUri(
                "https://generativelanguage.googleapis.com",
                "gemini-2.0-flash",
                "test-api-key"
        );

        assertEquals("/v1beta/models/gemini-2.0-flash:generateContent", uri.getPath());
    }

    @Test
    void shouldSendGenerateContentRequestWithContentsPartsAndTextShape() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiHttpClient geminiHttpClient = new GeminiHttpClient(builder);

        server.expect(once(), request -> {
                    assertEquals(HttpMethod.POST, request.getMethod());
                    assertEquals("/v1beta/models/gemini-1.5-flash:generateContent", request.getURI().getPath());
                    assertEquals("key=test-api-key", request.getURI().getQuery());
                })
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "contents": [
                            {
                              "parts": [
                                {
                                  "text": "Teach me BFS"
                                }
                              ]
                            }
                          ],
                          "generationConfig": {
                            "responseMimeType": "application/json"
                          }
                        }
                        """))
                .andRespond(withSuccess("""
                        {
                          "candidates": [
                            {
                              "content": {
                                "parts": [
                                  {
                                    "text": "{\\\"title\\\":\\\"BFS\\\"}"
                                  }
                                ]
                              }
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        String response = geminiHttpClient.generateContent(
                "https://generativelanguage.googleapis.com",
                "gemini-1.5-flash",
                "test-api-key",
                "Teach me BFS"
        );

        assertEquals("{\"title\":\"BFS\"}", response);
        server.verify();
    }

    @Test
    void shouldExtractSanitizedTextFromSuccessfulGeminiResponse() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiHttpClient geminiHttpClient = new GeminiHttpClient(builder);

        server.expect(once(), request -> {
                })
                .andRespond(withSuccess("""
                        {
                          "candidates": [
                            {
                              "content": {
                                "parts": [
                                  {
                                    "text": "```json\\n{\\n  \\"title\\": \\"DFS\\"\\n}\\n```"
                                  }
                                ]
                              }
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        String response = geminiHttpClient.generateContent(
                "https://generativelanguage.googleapis.com/v1beta",
                "gemini-1.5-flash",
                "test-api-key",
                "Teach me DFS"
        );

        assertEquals("""
                {
                  "title": "DFS"
                }
                """.trim(), response);
        server.verify();
    }

    @Test
    void shouldMapHttpFailureToSafeGeminiRequestFailure() {
        assertHttpFailureMapsToSafeRequestFailure(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldMapUnauthorizedHttpFailureToSafeGeminiRequestFailure() {
        assertHttpFailureMapsToSafeRequestFailure(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldMapForbiddenHttpFailureToSafeGeminiRequestFailure() {
        assertHttpFailureMapsToSafeRequestFailure(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldMapNotFoundHttpFailureToSafeGeminiRequestFailure() {
        assertHttpFailureMapsToSafeRequestFailure(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldMapTooManyRequestsHttpFailureToSafeGeminiRequestFailure() {
        assertHttpFailureMapsToSafeRequestFailure(HttpStatus.TOO_MANY_REQUESTS);
    }

    @Test
    void shouldMapServerErrorHttpFailureToSafeGeminiRequestFailure() {
        assertHttpFailureMapsToSafeRequestFailure(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void shouldExposeStatusFamilyOnHttpFailure() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiHttpClient geminiHttpClient = new GeminiHttpClient(builder);

        server.expect(once(), request -> {
                })
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"invalid request\"}"));

        GeminiException exception = assertThrows(
                GeminiException.class,
                () -> geminiHttpClient.generateContent(
                        "https://generativelanguage.googleapis.com",
                        "gemini-1.5-flash",
                        "test-api-key",
                        "Teach me trees"
                )
        );

        assertEquals(GeminiException.Category.REQUEST_FAILURE, exception.getCategory());
        assertEquals(400, exception.getHttpStatusCode());
        assertEquals("4xx", exception.getHttpStatusFamily());
        assertTrue(exception.getMessage().contains("HTTP status 400"));
        assertTrue(exception.getMessage().contains("4xx"));
        server.verify();
    }

    @Test
    void shouldMapEmptyGeminiResponseToEmptyResponseCategory() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiHttpClient geminiHttpClient = new GeminiHttpClient(builder);

        server.expect(once(), request -> {
                })
                .andRespond(withSuccess("""
                        {
                          "candidates": []
                        }
                        """, MediaType.APPLICATION_JSON));

        GeminiException exception = assertThrows(
                GeminiException.class,
                () -> geminiHttpClient.generateContent(
                        "https://generativelanguage.googleapis.com",
                        "gemini-1.5-flash",
                        "test-api-key",
                        "Teach me heaps"
                )
        );

        assertEquals(GeminiException.Category.EMPTY_RESPONSE_TEXT, exception.getCategory());
        assertNull(exception.getHttpStatusCode());
        server.verify();
    }

    @Test
    void shouldSanitizeProseWrappedJson() {
        String sanitized = GeminiHttpClient.sanitizeGeneratedText("""
                Here is your JSON response:
                {
                  "title": "Trees"
                }
                """);

        assertEquals("""
                {
                  "title": "Trees"
                }
                """.trim(), sanitized);
    }

    private void assertHttpFailureMapsToSafeRequestFailure(HttpStatus status) {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeminiHttpClient geminiHttpClient = new GeminiHttpClient(builder);

        server.expect(once(), request -> {
                })
                .andRespond(withStatus(status)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":\"request failed\"}"));

        GeminiException exception = assertThrows(
                GeminiException.class,
                () -> geminiHttpClient.generateContent(
                        "https://generativelanguage.googleapis.com",
                        "gemini-1.5-flash",
                        "test-api-key",
                        "Teach me maps"
                )
        );

        assertEquals(GeminiException.Category.REQUEST_FAILURE, exception.getCategory());
        assertEquals(status.value(), exception.getHttpStatusCode());
        assertEquals((status.value() / 100) + "xx", exception.getHttpStatusFamily());
        assertTrue(exception.getMessage().contains("HTTP status " + status.value()));
        server.verify();
    }
}
