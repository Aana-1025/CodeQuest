package com.codequest.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class ResponseParserTest {

    private ResponseParser responseParser;

    @BeforeEach
    void setUp() {
        responseParser = new ResponseParser(new ObjectMapper());
    }

    @Test
    void shouldRejectMalformedJson() {
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCourseResponse("{invalid json")
        );

        assertEquals("Malformed AI JSON response.", exception.getMessage());
    }

    @Test
    void shouldRejectMissingTitle() {
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCourseResponse(validCourseJson().replace("\"title\": \"Binary Search\",\n", ""))
        );

        assertEquals("title is required.", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidDifficulty() {
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCourseResponse(validCourseJson().replace("\"difficulty\": \"BEGINNER\"", "\"difficulty\": \"EXPERT\""))
        );

        assertEquals("difficulty has an invalid value.", exception.getMessage());
    }

    @Test
    void shouldRejectEmptyLevels() {
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCourseResponse(validCourseJson().replace(levelsBlock(), "\"levels\": []"))
        );

        assertEquals("levels must contain at least 1 item.", exception.getMessage());
    }

    @Test
    void shouldRejectDuplicateOrderNumbers() {
        String duplicateOrderJson = """
                {
                  "title": "Binary Search",
                  "description": "A beginner-friendly course on binary search.",
                  "difficulty": "BEGINNER",
                  "levels": [
                    {
                      "title": "Level One",
                      "contentMarkdown": "#################### valid content block",
                      "orderNumber": 1,
                      "isBoss": false,
                      "xpReward": 50
                    },
                    {
                      "title": "Level Two",
                      "contentMarkdown": "#################### another valid content block",
                      "orderNumber": 1,
                      "isBoss": true,
                      "xpReward": 100
                    }
                  ]
                }
                """;

        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCourseResponse(duplicateOrderJson)
        );

        assertEquals("level.orderNumber values must be unique.", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidQuizCorrectAnswer() {
        String invalidQuizJson = validCourseJson().replace("\"correctAnswer\": \"B\"", "\"correctAnswer\": \"Z\"");

        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCourseResponse(invalidQuizJson)
        );

        assertEquals("quiz.correctAnswer has an invalid value.", exception.getMessage());
    }

    @Test
    void shouldRejectLevelXpRewardOutsideAllowedRange() {
        String invalidXpJson = validCourseJson().replace("\"xpReward\": 50,", "\"xpReward\": 0,");

        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCourseResponse(invalidXpJson)
        );

        assertEquals("level.xpReward must be between 1 and 500.", exception.getMessage());
    }

    @Test
    void shouldParseValidCourseResponse() {
        AiCourseResponse response = responseParser.parseCourseResponse(validCourseJson());

        assertNotNull(response);
        assertEquals("Binary Search", response.title());
        assertEquals("BEGINNER", response.difficulty());
        assertEquals(1, response.levels().size());
        assertEquals(1, response.levels().get(0).orderNumber());
        assertEquals("B", response.levels().get(0).quiz().get(0).correctAnswer());
        assertEquals("EASY", response.levels().get(0).codingProblems().get(0).difficulty());
    }

    @Test
    void shouldKeepValidationMessageSafeWithoutEchoingFullPayload() {
        String unsafePayload = """
                {
                  "title": "x",
                  "description": "short",
                  "difficulty": "BEGINNER",
                  "levels": []
                }
                """;

        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCourseResponse(unsafePayload)
        );

        assertFalse(exception.getMessage().contains("\"title\": \"x\""));
        assertTrue(exception.getMessage().contains("title"));
    }

    private String validCourseJson() {
        return """
                {
                  "title": "Binary Search",
                  "description": "A beginner-friendly course on binary search.",
                  "difficulty": "BEGINNER",
                  "levels": [
                    {
                      "title": "What is Binary Search?",
                      "contentMarkdown": "# Binary Search\\nThis lesson explains sorted search spaces clearly.",
                      "orderNumber": 1,
                      "isBoss": false,
                      "xpReward": 50,
                      "flashcards": [
                        {
                          "front": "What condition does binary search need?",
                          "back": "The search space must be sorted or monotonic."
                        }
                      ],
                      "quiz": [
                        {
                          "question": "Binary search works best on...",
                          "optionA": "Unsorted data",
                          "optionB": "Sorted or monotonic data",
                          "optionC": "Random strings only",
                          "optionD": "Images only",
                          "correctAnswer": "B",
                          "explanation": "Binary search repeatedly halves a sorted or monotonic search space.",
                          "conceptTag": "binary-search-basics",
                          "xpReward": 10
                        }
                      ],
                      "codingProblems": [
                        {
                          "title": "Find Target with Binary Search",
                          "description": "Return the index of target in a sorted array, or -1 if missing.",
                          "difficulty": "EASY",
                          "xpReward": 50
                        }
                      ]
                    }
                  ]
                }
                """;
    }

    private String levelsBlock() {
        return """
                  "levels": [
                    {
                      "title": "What is Binary Search?",
                      "contentMarkdown": "# Binary Search\\nThis lesson explains sorted search spaces clearly.",
                      "orderNumber": 1,
                      "isBoss": false,
                      "xpReward": 50,
                      "flashcards": [
                        {
                          "front": "What condition does binary search need?",
                          "back": "The search space must be sorted or monotonic."
                        }
                      ],
                      "quiz": [
                        {
                          "question": "Binary search works best on...",
                          "optionA": "Unsorted data",
                          "optionB": "Sorted or monotonic data",
                          "optionC": "Random strings only",
                          "optionD": "Images only",
                          "correctAnswer": "B",
                          "explanation": "Binary search repeatedly halves a sorted or monotonic search space.",
                          "conceptTag": "binary-search-basics",
                          "xpReward": 10
                        }
                      ],
                      "codingProblems": [
                        {
                          "title": "Find Target with Binary Search",
                          "description": "Return the index of target in a sorted array, or -1 if missing.",
                          "difficulty": "EASY",
                          "xpReward": 50
                        }
                      ]
                    }
                  ]
                """;
    }
}
