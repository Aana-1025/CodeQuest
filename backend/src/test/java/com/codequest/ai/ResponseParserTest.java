package com.codequest.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.codequest.ai.dto.ReviewCodeResponse;
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
        assertEquals("public class Main {}", response.levels().get(0).codingProblems().get(0).starterCode().get("java"));
        assertEquals("", response.levels().get(0).codingProblems().get(0).sampleTestCases().get(0).get("stdin"));
        assertEquals("6", response.levels().get(0).codingProblems().get(0).hiddenTests().get(0).get("expectedOutput"));
    }

    @Test
    void shouldRejectCodingProblemWithoutStarterCode() {
        String invalidCodingProblemJson = validCourseJson().replace(starterCodeBlock(), "\"starterCode\": null");

        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCourseResponse(invalidCodingProblemJson)
        );

        assertEquals("codingProblem.starterCode is required.", exception.getMessage());
    }

    @Test
    void shouldRejectCodingProblemWithoutHiddenTests() {
        String invalidCodingProblemJson = validCourseJson().replace(hiddenTestsBlock(), "\"hiddenTests\": []");

        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCourseResponse(invalidCodingProblemJson)
        );

        assertEquals("codingProblem.hiddenTests must contain at least 1 item.", exception.getMessage());
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

    @Test
    void shouldParseValidCodeReviewResponse() {
        ReviewCodeResponse response = responseParser.parseCodeReviewResponse(validCodeReviewJson());

        assertNotNull(response);
        assertEquals("O(log n)", response.timeComplexity());
        assertEquals("O(1)", response.spaceComplexity());
        assertTrue(response.correctnessIssues().isEmpty());
        assertEquals(2, response.improvements().size());
        assertEquals("Binary search is already the right approach for sorted input.", response.betterApproach());
        assertEquals("Good job choosing an efficient strategy.", response.encouragement());
    }

    @Test
    void shouldRejectEmptyCodeReviewResponse() {
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCodeReviewResponse(" ")
        );

        assertEquals("AI response must not be blank.", exception.getMessage());
    }

    @Test
    void shouldRejectMissingTimeComplexity() {
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCodeReviewResponse(validCodeReviewJson().replace("\"timeComplexity\": \"O(log n)\",\n", ""))
        );

        assertEquals("timeComplexity is required.", exception.getMessage());
    }

    @Test
    void shouldRejectMissingSpaceComplexity() {
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCodeReviewResponse(validCodeReviewJson().replace("\"spaceComplexity\": \"O(1)\",\n", ""))
        );

        assertEquals("spaceComplexity is required.", exception.getMessage());
    }

    @Test
    void shouldRejectMissingCorrectnessIssues() {
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCodeReviewResponse(validCodeReviewJson().replace("\"correctnessIssues\": [],\n", ""))
        );

        assertEquals("correctnessIssues is required.", exception.getMessage());
    }

    @Test
    void shouldRejectMissingImprovements() {
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCodeReviewResponse(validCodeReviewJson().replace(improvementsBlock(), ""))
        );

        assertEquals("improvements is required.", exception.getMessage());
    }

    @Test
    void shouldRejectMissingBetterApproach() {
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCodeReviewResponse(validCodeReviewJson().replace("\"betterApproach\": \"Binary search is already the right approach for sorted input.\",\n", ""))
        );

        assertEquals("betterApproach is required.", exception.getMessage());
    }

    @Test
    void shouldRejectMissingEncouragement() {
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCodeReviewResponse(validCodeReviewJson().replace(
                        ",\n  \"encouragement\": \"Good job choosing an efficient strategy.\"",
                        ""
                ))
        );

        assertEquals("encouragement is required.", exception.getMessage());
    }

    @Test
    void shouldRejectBlankRequiredStringInCodeReview() {
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCodeReviewResponse(validCodeReviewJson().replace("\"timeComplexity\": \"O(log n)\"", "\"timeComplexity\": \"  \""))
        );

        assertEquals("timeComplexity must be between 1 and 200 characters.", exception.getMessage());
    }

    @Test
    void shouldRejectNullCorrectnessIssuesList() {
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCodeReviewResponse(validCodeReviewJson().replace("\"correctnessIssues\": []", "\"correctnessIssues\": null"))
        );

        assertEquals("correctnessIssues is required.", exception.getMessage());
    }

    @Test
    void shouldRejectTooManyCorrectnessIssues() {
        String tooManyIssues = "\"correctnessIssues\": [\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\",\"10\",\"11\"]";
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCodeReviewResponse(validCodeReviewJson().replace("\"correctnessIssues\": []", tooManyIssues))
        );

        assertEquals("correctnessIssues must contain at most 10 items.", exception.getMessage());
    }

    @Test
    void shouldRejectTooManyImprovements() {
        String tooManyImprovements = """
                  "improvements": [
                    "1",
                    "2",
                    "3",
                    "4",
                    "5",
                    "6",
                    "7",
                    "8",
                    "9",
                    "10",
                    "11"
                  ],
                """.trim();
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCodeReviewResponse(validCodeReviewJson().replace(improvementsBlock().trim(), tooManyImprovements))
        );

        assertEquals("improvements must contain at most 10 items.", exception.getMessage());
    }

    @Test
    void shouldRejectBlankListItem() {
        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCodeReviewResponse(validCodeReviewJson().replace("\"Handle overflow in mid calculation.\"", "\"  \""))
        );

        assertEquals("improvements item must be between 1 and 500 characters.", exception.getMessage());
    }

    @Test
    void shouldKeepCodeReviewValidationMessageSafeWithoutEchoingFullPayload() {
        String unsafePayload = """
                {
                  "timeComplexity": " ",
                  "spaceComplexity": "O(1)",
                  "correctnessIssues": [],
                  "improvements": [],
                  "betterApproach": "x",
                  "encouragement": "y"
                }
                """;

        AiResponseValidationException exception = assertThrows(
                AiResponseValidationException.class,
                () -> responseParser.parseCodeReviewResponse(unsafePayload)
        );

        assertFalse(exception.getMessage().contains("\"timeComplexity\": \" \""));
        assertTrue(exception.getMessage().contains("timeComplexity"));
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
                          "xpReward": 50,
                          "starterCode": {
                            "java": "public class Main {}",
                            "python": "def solve():\\n    pass",
                            "javascript": "function solve() {}",
                            "cpp": "int main() { return 0; }"
                          },
                          "sampleTestCases": [
                            {
                              "stdin": "",
                              "expectedOutput": "4"
                            }
                          ],
                          "hiddenTests": [
                            {
                              "stdin": "",
                              "expectedOutput": "6"
                            }
                          ]
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
                          "xpReward": 50,
                          "starterCode": {
                            "java": "public class Main {}",
                            "python": "def solve():\\n    pass",
                            "javascript": "function solve() {}",
                            "cpp": "int main() { return 0; }"
                          },
                          "sampleTestCases": [
                            {
                              "stdin": "",
                              "expectedOutput": "4"
                            }
                          ],
                          "hiddenTests": [
                            {
                              "stdin": "",
                              "expectedOutput": "6"
                            }
                          ]
                        }
                      ]
                    }
                  ]
                """;
    }

    private String validCodeReviewJson() {
        return """
                {
                  "timeComplexity": "O(log n)",
                  "spaceComplexity": "O(1)",
                  "correctnessIssues": [],
                  "improvements": [
                    "Handle overflow in mid calculation.",
                    "Consider explicitly handling empty arrays."
                  ],
                  "betterApproach": "Binary search is already the right approach for sorted input.",
                  "encouragement": "Good job choosing an efficient strategy."
                }
                """;
    }

    private String starterCodeBlock() {
        return """
                          "starterCode": {
                            "java": "public class Main {}",
                            "python": "def solve():\\n    pass",
                            "javascript": "function solve() {}",
                            "cpp": "int main() { return 0; }"
                          }
                """.trim();
    }

    private String hiddenTestsBlock() {
        return """
                          "hiddenTests": [
                            {
                              "stdin": "",
                              "expectedOutput": "6"
                            }
                          ]
                """.trim();
    }

    private String improvementsBlock() {
        return """
                  "improvements": [
                    "Handle overflow in mid calculation.",
                    "Consider explicitly handling empty arrays."
                  ],
                """;
    }
}
