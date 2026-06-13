package com.codequest.ai;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.codequest.ai.dto.ReviewCodeResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ResponseParser {

    private static final Set<String> COURSE_DIFFICULTIES = Set.of("BEGINNER", "INTERMEDIATE", "ADVANCED");
    private static final Set<String> QUIZ_ANSWERS = Set.of("A", "B", "C", "D");
    private static final Set<String> CODING_DIFFICULTIES = Set.of("EASY", "MEDIUM", "HARD");
    private static final Set<String> CODING_LANGUAGES = Set.of("java", "python", "javascript", "cpp");

    private final ObjectMapper objectMapper;

    public ResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AiCourseResponse parseCourseResponse(String rawJson) {
        if (rawJson == null || rawJson.isBlank()) {
            throw new AiResponseValidationException("AI response must not be blank.");
        }

        try {
            AiCourseResponse response = objectMapper.readValue(rawJson, AiCourseResponse.class);
            validateCourseResponse(response);
            return response;
        } catch (JsonProcessingException ex) {
            throw new AiResponseValidationException("Malformed AI JSON response.", ex);
        }
    }

    public ReviewCodeResponse parseCodeReviewResponse(String rawJson) {
        if (rawJson == null || rawJson.isBlank()) {
            throw new AiResponseValidationException("AI response must not be blank.");
        }

        try {
            ReviewCodeResponse response = objectMapper.readValue(rawJson, ReviewCodeResponse.class);
            validateCodeReviewResponse(response);
            return response;
        } catch (JsonProcessingException ex) {
            throw new AiResponseValidationException("Malformed AI JSON response.", ex);
        }
    }

    private void validateCourseResponse(AiCourseResponse response) {
        if (response == null) {
            throw new AiResponseValidationException("AI response must be a JSON object.");
        }

        validateTrimmedLength(response.title(), "title", 2, 120);
        validateTrimmedLength(response.description(), "description", 10, 1000);
        validateEnumValue(response.difficulty(), "difficulty", COURSE_DIFFICULTIES);

        List<AiLevelResponse> levels = response.levels();
        if (levels == null || levels.isEmpty()) {
            throw new AiResponseValidationException("levels must contain at least 1 item.");
        }
        if (levels.size() > 10) {
            throw new AiResponseValidationException("levels must contain at most 10 items.");
        }

        Set<Integer> orderNumbers = new HashSet<>();
        for (AiLevelResponse level : levels) {
            validateLevel(level, orderNumbers);
        }
    }

    private void validateCodeReviewResponse(ReviewCodeResponse response) {
        if (response == null) {
            throw new AiResponseValidationException("AI response must be a JSON object.");
        }

        validateTrimmedLength(response.timeComplexity(), "timeComplexity", 1, 200);
        validateTrimmedLength(response.spaceComplexity(), "spaceComplexity", 1, 200);
        validateTrimmedLength(response.betterApproach(), "betterApproach", 1, 2000);
        validateTrimmedLength(response.encouragement(), "encouragement", 1, 500);
        validateStringList(response.correctnessIssues(), "correctnessIssues", 10, 500);
        validateStringList(response.improvements(), "improvements", 10, 500);
    }

    private void validateLevel(AiLevelResponse level, Set<Integer> orderNumbers) {
        if (level == null) {
            throw new AiResponseValidationException("levels must not contain null items.");
        }

        validateTrimmedLength(level.title(), "level.title", 2, 120);
        validateTrimmedLength(level.contentMarkdown(), "level.contentMarkdown", 20, 8000);

        Integer orderNumber = level.orderNumber();
        if (orderNumber == null || orderNumber <= 0) {
            throw new AiResponseValidationException("level.orderNumber must be a positive integer.");
        }
        if (!orderNumbers.add(orderNumber)) {
            throw new AiResponseValidationException("level.orderNumber values must be unique.");
        }

        if (level.isBoss() == null) {
            throw new AiResponseValidationException("level.isBoss is required.");
        }

        validateXpReward(level.xpReward(), "level.xpReward", 1, 500);
        validateFlashcards(level.flashcards());
        validateQuiz(level.quiz());
        validateCodingProblems(level.codingProblems());
    }

    private void validateFlashcards(List<AiFlashcardResponse> flashcards) {
        if (flashcards == null) {
            return;
        }

        for (AiFlashcardResponse flashcard : flashcards) {
            if (flashcard == null) {
                throw new AiResponseValidationException("flashcards must not contain null items.");
            }
            validateTrimmedLength(flashcard.front(), "flashcard.front", 1, 500);
            validateTrimmedLength(flashcard.back(), "flashcard.back", 1, 1000);
        }
    }

    private void validateQuiz(List<AiQuizQuestionResponse> quizQuestions) {
        if (quizQuestions == null) {
            return;
        }

        for (AiQuizQuestionResponse question : quizQuestions) {
            if (question == null) {
                throw new AiResponseValidationException("quiz must not contain null items.");
            }

            validateTrimmedLength(question.question(), "quiz.question", 1, 500);
            validateTrimmedLength(question.optionA(), "quiz.optionA", 1, 300);
            validateTrimmedLength(question.optionB(), "quiz.optionB", 1, 300);
            validateTrimmedLength(question.optionC(), "quiz.optionC", 1, 300);
            validateTrimmedLength(question.optionD(), "quiz.optionD", 1, 300);
            validateEnumValue(question.correctAnswer(), "quiz.correctAnswer", QUIZ_ANSWERS);
            validateTrimmedLength(question.explanation(), "quiz.explanation", 1, 1000);
            validateTrimmedLength(question.conceptTag(), "quiz.conceptTag", 1, 100);
            validateXpReward(question.xpReward(), "quiz.xpReward", 1, 500);
        }
    }

    private void validateCodingProblems(List<AiCodingProblemResponse> codingProblems) {
        if (codingProblems == null) {
            return;
        }

        for (AiCodingProblemResponse codingProblem : codingProblems) {
            if (codingProblem == null) {
                throw new AiResponseValidationException("codingProblems must not contain null items.");
            }

            validateTrimmedLength(codingProblem.title(), "codingProblem.title", 2, 120);
            validateTrimmedLength(codingProblem.description(), "codingProblem.description", 10, 2000);
            validateEnumValue(codingProblem.difficulty(), "codingProblem.difficulty", CODING_DIFFICULTIES);
            validateXpReward(codingProblem.xpReward(), "codingProblem.xpReward", 1, 500);
            validateStarterCode(codingProblem.starterCode());
            validateTestCases(codingProblem.sampleTestCases(), "codingProblem.sampleTestCases");
            validateTestCases(codingProblem.hiddenTests(), "codingProblem.hiddenTests");
        }
    }

    private void validateStarterCode(Map<String, String> starterCode) {
        if (starterCode == null || starterCode.isEmpty()) {
            throw new AiResponseValidationException("codingProblem.starterCode is required.");
        }

        for (String language : CODING_LANGUAGES) {
            if (!starterCode.containsKey(language)) {
                throw new AiResponseValidationException("codingProblem.starterCode must include " + language + ".");
            }

            validateTrimmedLength(starterCode.get(language), "codingProblem.starterCode." + language, 1, 20000);
        }
    }

    private void validateTestCases(List<Map<String, String>> testCases, String fieldName) {
        if (testCases == null || testCases.isEmpty()) {
            throw new AiResponseValidationException(fieldName + " must contain at least 1 item.");
        }

        for (Map<String, String> testCase : testCases) {
            if (testCase == null) {
                throw new AiResponseValidationException(fieldName + " must not contain null items.");
            }

            if (!testCase.containsKey("stdin")) {
                throw new AiResponseValidationException(fieldName + " item.stdin is required.");
            }
            if (!testCase.containsKey("expectedOutput")) {
                throw new AiResponseValidationException(fieldName + " item.expectedOutput is required.");
            }

            validateTrimmedLength(testCase.get("stdin"), fieldName + " item.stdin", 0, 4000);
            validateTrimmedLength(testCase.get("expectedOutput"), fieldName + " item.expectedOutput", 1, 4000);
        }
    }

    private void validateTrimmedLength(String value, String fieldName, int minLength, int maxLength) {
        if (value == null) {
            throw new AiResponseValidationException(fieldName + " is required.");
        }

        String trimmedValue = value.trim();
        if (trimmedValue.length() < minLength || trimmedValue.length() > maxLength) {
            throw new AiResponseValidationException(
                    fieldName + " must be between " + minLength + " and " + maxLength + " characters."
            );
        }
    }

    private void validateEnumValue(String value, String fieldName, Set<String> allowedValues) {
        if (value == null || !allowedValues.contains(value.trim())) {
            throw new AiResponseValidationException(fieldName + " has an invalid value.");
        }
    }

    private void validateXpReward(Integer xpReward, String fieldName, int minValue, int maxValue) {
        if (xpReward == null || xpReward < minValue || xpReward > maxValue) {
            throw new AiResponseValidationException(
                    fieldName + " must be between " + minValue + " and " + maxValue + "."
            );
        }
    }

    private void validateStringList(List<String> values, String fieldName, int maxItems, int maxItemLength) {
        if (values == null) {
            throw new AiResponseValidationException(fieldName + " is required.");
        }
        if (values.size() > maxItems) {
            throw new AiResponseValidationException(fieldName + " must contain at most " + maxItems + " items.");
        }

        for (String value : values) {
            validateTrimmedLength(value, fieldName + " item", 1, maxItemLength);
        }
    }
}
