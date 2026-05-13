package com.codequest.course;

import java.time.Instant;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codequest.ai.AiCourseResponse;
import com.codequest.ai.AiResponseValidationException;
import com.codequest.ai.GeminiException;
import com.codequest.ai.AiLevelResponse;
import com.codequest.ai.AiQuizQuestionResponse;
import com.codequest.ai.GeminiService;
import com.codequest.ai.ResponseParser;
import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.course.dto.CourseLevelResponse;
import com.codequest.course.dto.CourseResponse;
import com.codequest.course.dto.CourseLevelSummaryResponse;
import com.codequest.course.dto.GenerateCourseRequest;
import com.codequest.course.dto.GenerateCourseResponse;
import com.codequest.level.Level;
import com.codequest.level.LevelRepository;
import com.codequest.quiz.Quiz;
import com.codequest.quiz.QuizRepository;
import com.codequest.quiz.dto.QuizOptionsResponse;
import com.codequest.quiz.dto.QuizQuestionResponse;
import com.codequest.user.User;
import com.codequest.user.UserRepository;

@Service
public class CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);
    private static final int INITIAL_GEMINI_ATTEMPT = 1;
    private static final int MAX_GEMINI_ATTEMPTS = 2;

    private final CourseRepository courseRepository;
    private final LevelRepository levelRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final ResponseParser responseParser;

    public CourseService(CourseRepository courseRepository, LevelRepository levelRepository, QuizRepository quizRepository,
                         UserRepository userRepository, GeminiService geminiService, ResponseParser responseParser) {
        this.courseRepository = courseRepository;
        this.levelRepository = levelRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.geminiService = geminiService;
        this.responseParser = responseParser;
    }

    @Transactional
    public GenerateCourseResponse generateCourse(UUID creatorId, GenerateCourseRequest request) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "User not found."));

        return generateCourse(creator, request);
    }

    @Transactional
    public GenerateCourseResponse generateCourse(User creator, GenerateCourseRequest request) {
        String normalizedTopic = normalizeTopic(request.topic());

        return courseRepository.findByNormalizedTopicAndDifficulty(normalizedTopic, request.difficulty())
                .map(course -> toGenerateCourseResponse(course, true))
                .orElseGet(() -> createCourseWithSafeFallback(creator, normalizedTopic, request));
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "Course not found."));

        List<Level> orderedLevels = levelRepository.findByCourseIdOrderByOrderNumberAsc(course.getId());
        if (orderedLevels.isEmpty()) {
            orderedLevels = course.getLevels().stream()
                    .sorted(Comparator.comparing(Level::getOrderNumber))
                    .collect(Collectors.toList());
        }

        Map<UUID, List<QuizQuestionResponse>> quizQuestionsByLevelId = getQuizQuestionsByLevelId(orderedLevels);
        List<CourseLevelResponse> levels = orderedLevels.stream()
                .map(level -> toCourseLevelResponse(level, quizQuestionsByLevelId.getOrDefault(level.getId(), List.of())))
                .collect(Collectors.toList());

        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDifficulty(),
                course.getSourceType(),
                course.getTotalXp(),
                levels
        );
    }

    private GenerateCourseResponse createCourseWithSafeFallback(User creator, String normalizedTopic, GenerateCourseRequest request) {
        boolean geminiConfigured = geminiService.isConfigured();
        if (!geminiConfigured) {
            logFallbackReason(normalizedTopic, request.difficulty(), geminiConfigured, determineFallbackReason(false, null), null);
            return createPlaceholderCourse(creator, normalizedTopic, request.difficulty());
        }

        try {
            String rawAiResponse = generateCourseJsonWithRetry(request);
            AiCourseResponse aiCourseResponse = responseParser.parseCourseResponse(rawAiResponse);
            validateRequestedDifficulty(request.difficulty(), aiCourseResponse.difficulty());
            return createAiCourse(creator, normalizedTopic, request.difficulty(), aiCourseResponse);
        } catch (RuntimeException ex) {
            logFallbackReason(
                    normalizedTopic,
                    request.difficulty(),
                    geminiConfigured,
                    determineFallbackReason(true, ex),
                    ex
            );
        }

        return createPlaceholderCourse(creator, normalizedTopic, request.difficulty());
    }

    private String generateCourseJsonWithRetry(GenerateCourseRequest request) {
        RuntimeException lastFailure = null;

        for (int attempt = INITIAL_GEMINI_ATTEMPT; attempt <= MAX_GEMINI_ATTEMPTS; attempt++) {
            try {
                return geminiService.generateCourseJson(request);
            } catch (RuntimeException ex) {
                lastFailure = ex;
                if (!shouldRetryGeminiRequest(ex) || attempt == MAX_GEMINI_ATTEMPTS) {
                    throw ex;
                }

                logger.info(buildRetryDiagnosticMessage(request.topic(), request.difficulty(), ex, attempt));
            }
        }

        throw lastFailure == null
                ? new GeminiException(GeminiException.Category.UNEXPECTED_GEMINI_ERROR, "Gemini retry failed unexpectedly.")
                : lastFailure;
    }

    private GenerateCourseResponse createPlaceholderCourse(User creator, String normalizedTopic, CourseDifficulty difficulty) {
        Instant now = Instant.now();
        String title = toDisplayTitle(normalizedTopic);

        Course course = new Course(
                UUID.randomUUID(),
                normalizedTopic,
                title,
                "A CodeQuest course foundation for " + title + ".",
                creator,
                difficulty,
                false,
                225,
                CourseSourceType.PLACEHOLDER,
                now,
                now
        );

        course.addLevel(new Level(
                UUID.randomUUID(),
                null,
                "Introduction to " + title,
                "# Introduction to " + title + "\n\nThis is a deterministic placeholder lesson for " + title + ".",
                1,
                false,
                50,
                now,
                now
        ));
        course.addLevel(new Level(
                UUID.randomUUID(),
                null,
                "Practice " + title,
                "# Practice " + title + "\n\nUse this placeholder practice level to explore " + title + ".",
                2,
                false,
                75,
                now,
                now
        ));
        course.addLevel(new Level(
                UUID.randomUUID(),
                null,
                title + " Boss Challenge",
                "# " + title + " Boss Challenge\n\nThis placeholder boss level marks the end of the foundation course.",
                3,
                true,
                100,
                now,
                now
        ));

        Course savedCourse = courseRepository.save(course);
        return toGenerateCourseResponse(savedCourse, false);
    }

    private GenerateCourseResponse createAiCourse(User creator, String normalizedTopic, CourseDifficulty difficulty,
                                                  AiCourseResponse aiCourseResponse) {
        Instant now = Instant.now();
        List<AiLevelResponse> orderedAiLevels = aiCourseResponse.levels().stream()
                .sorted(Comparator.comparing(AiLevelResponse::orderNumber))
                .toList();

        int totalXp = orderedAiLevels.stream()
                .mapToInt(AiLevelResponse::xpReward)
                .sum();

        Course course = new Course(
                UUID.randomUUID(),
                normalizedTopic,
                aiCourseResponse.title().trim(),
                aiCourseResponse.description().trim(),
                creator,
                difficulty,
                false,
                totalXp,
                CourseSourceType.AI,
                now,
                now
        );

        for (AiLevelResponse aiLevel : orderedAiLevels) {
            course.addLevel(new Level(
                    UUID.randomUUID(),
                    null,
                    aiLevel.title().trim(),
                    aiLevel.contentMarkdown().trim(),
                    aiLevel.orderNumber(),
                    aiLevel.isBoss(),
                    aiLevel.xpReward(),
                    now,
                    now
            ));
        }

        Course savedCourse = courseRepository.save(course);
        persistAiQuizzes(savedCourse.getLevels(), orderedAiLevels, now);
        return toGenerateCourseResponse(savedCourse, false);
    }

    private GenerateCourseResponse toGenerateCourseResponse(Course course, boolean cacheHit) {
        List<Level> orderedLevels = levelRepository.findByCourseIdOrderByOrderNumberAsc(course.getId());
        if (orderedLevels.isEmpty()) {
            orderedLevels = course.getLevels().stream()
                    .sorted(Comparator.comparing(Level::getOrderNumber))
                    .collect(Collectors.toList());
        }

        List<CourseLevelSummaryResponse> levels = orderedLevels.stream()
                .map(this::toCourseLevelSummaryResponse)
                .collect(Collectors.toList());

        return new GenerateCourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getSourceType(),
                cacheHit,
                levels
        );
    }

    private CourseLevelSummaryResponse toCourseLevelSummaryResponse(Level level) {
        return new CourseLevelSummaryResponse(
                level.getId(),
                level.getTitle(),
                level.getOrderNumber(),
                level.isBoss(),
                level.getXpReward()
        );
    }

    private CourseLevelResponse toCourseLevelResponse(Level level, List<QuizQuestionResponse> quizQuestions) {
        return new CourseLevelResponse(
                level.getId(),
                level.getOrderNumber(),
                level.getTitle(),
                level.getContentMarkdown(),
                level.getXpReward(),
                level.isBoss(),
                quizQuestions
        );
    }

    private void persistAiQuizzes(List<Level> savedLevels, List<AiLevelResponse> orderedAiLevels, Instant now) {
        if (savedLevels.isEmpty() || orderedAiLevels.isEmpty()) {
            return;
        }

        Map<Integer, Level> levelsByOrderNumber = savedLevels.stream()
                .collect(Collectors.toMap(Level::getOrderNumber, level -> level));

        List<Quiz> quizzesToPersist = orderedAiLevels.stream()
                .flatMap(aiLevel -> buildQuizEntitiesForLevel(aiLevel, levelsByOrderNumber.get(aiLevel.orderNumber()), now).stream())
                .toList();

        if (!quizzesToPersist.isEmpty()) {
            quizRepository.saveAll(quizzesToPersist);
        }
    }

    private List<Quiz> buildQuizEntitiesForLevel(AiLevelResponse aiLevel, Level savedLevel, Instant now) {
        if (savedLevel == null || aiLevel.quiz() == null || aiLevel.quiz().isEmpty()) {
            return List.of();
        }

        List<AiQuizQuestionResponse> quizQuestions = aiLevel.quiz();
        return java.util.stream.IntStream.range(0, quizQuestions.size())
                .mapToObj(index -> toQuizEntity(savedLevel, quizQuestions.get(index), index + 1, now))
                .toList();
    }

    private Quiz toQuizEntity(Level savedLevel, AiQuizQuestionResponse question, int orderNumber, Instant now) {
        return new Quiz(
                UUID.randomUUID(),
                savedLevel,
                orderNumber,
                question.question().trim(),
                question.optionA().trim(),
                question.optionB().trim(),
                question.optionC().trim(),
                question.optionD().trim(),
                question.correctAnswer().trim(),
                trimToNull(question.explanation()),
                trimToNull(question.conceptTag()),
                question.xpReward(),
                now,
                now
        );
    }

    private Map<UUID, List<QuizQuestionResponse>> getQuizQuestionsByLevelId(List<Level> orderedLevels) {
        if (orderedLevels.isEmpty()) {
            return Collections.emptyMap();
        }

        List<UUID> levelIds = orderedLevels.stream()
                .map(Level::getId)
                .toList();

        List<Quiz> quizzes = quizRepository.findByLevelIdInOrderByLevelIdAscOrderNumberAsc(levelIds);
        if (quizzes.isEmpty()) {
            return Collections.emptyMap();
        }

        return quizzes.stream()
                .collect(Collectors.groupingBy(
                        quiz -> quiz.getLevel().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(this::toQuizQuestionResponse, Collectors.toList())
                ));
    }

    private QuizQuestionResponse toQuizQuestionResponse(Quiz quiz) {
        return new QuizQuestionResponse(
                quiz.getId(),
                quiz.getOrderNumber(),
                quiz.getQuestion(),
                new QuizOptionsResponse(
                        quiz.getOptionA(),
                        quiz.getOptionB(),
                        quiz.getOptionC(),
                        quiz.getOptionD()
                ),
                quiz.getExplanation(),
                quiz.getConceptTag(),
                quiz.getXpReward()
        );
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeTopic(String topic) {
        return topic == null
                ? ""
                : topic.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    private void validateRequestedDifficulty(CourseDifficulty requestedDifficulty, String aiDifficulty) {
        if (!requestedDifficulty.name().equals(aiDifficulty)) {
            throw new IllegalArgumentException("AI difficulty did not match the requested difficulty.");
        }
    }

    AiFallbackReason determineFallbackReason(boolean geminiConfigured, RuntimeException ex) {
        if (!geminiConfigured) {
            return AiFallbackReason.MISSING_GEMINI_CONFIG;
        }
        if (ex instanceof GeminiException geminiException) {
            return switch (geminiException.getCategory()) {
                case CONFIG_MISSING -> AiFallbackReason.MISSING_GEMINI_CONFIG;
                case REQUEST_FAILURE -> AiFallbackReason.GEMINI_REQUEST_FAILURE;
                case EMPTY_RESPONSE_TEXT -> AiFallbackReason.EMPTY_GEMINI_RESPONSE_TEXT;
                case RESPONSE_EXTRACTION_FAILURE -> AiFallbackReason.RESPONSE_EXTRACTION_FAILURE;
                case UNEXPECTED_GEMINI_ERROR -> AiFallbackReason.UNEXPECTED_AI_INTEGRATION_ERROR;
            };
        }
        if (ex instanceof AiResponseValidationException) {
            return AiFallbackReason.PARSER_VALIDATION_FAILURE;
        }
        if (ex instanceof IllegalArgumentException) {
            return AiFallbackReason.REQUESTED_DIFFICULTY_MISMATCH;
        }
        return AiFallbackReason.UNEXPECTED_AI_INTEGRATION_ERROR;
    }

    private void logFallbackReason(String normalizedTopic, CourseDifficulty requestedDifficulty,
                                   boolean geminiConfigured, AiFallbackReason reason, RuntimeException ex) {
        logger.info(buildFallbackDiagnosticMessage(
                normalizedTopic,
                requestedDifficulty,
                geminiConfigured,
                reason,
                ex
        ));
    }

    String buildRetryDiagnosticMessage(String topic, CourseDifficulty requestedDifficulty, RuntimeException ex, int attempt) {
        String normalizedTopic = normalizeTopic(topic);
        String exceptionType = ex == null ? "None" : ex.getClass().getSimpleName();
        Integer httpStatusCode = extractHttpStatusCode(ex);
        String httpStatusFamily = extractHttpStatusFamily(ex);

        return "Retrying Gemini course generation after transient failure. topic='" + normalizedTopic + "'"
                + ", requestedDifficulty=" + requestedDifficulty
                + ", attempt=" + attempt
                + ", exceptionType=" + exceptionType
                + ", httpStatusCode=" + (httpStatusCode == null ? "None" : httpStatusCode)
                + ", httpStatusFamily=" + (httpStatusFamily == null ? "None" : httpStatusFamily);
    }

    private boolean shouldRetryGeminiRequest(RuntimeException ex) {
        if (!(ex instanceof GeminiException geminiException)) {
            return false;
        }

        return geminiException.getCategory() == GeminiException.Category.REQUEST_FAILURE
                && isServerError(geminiException.getHttpStatusCode());
    }

    private boolean isServerError(Integer httpStatusCode) {
        return httpStatusCode != null && httpStatusCode >= 500 && httpStatusCode < 600;
    }

    String buildFallbackDiagnosticMessage(String normalizedTopic, CourseDifficulty requestedDifficulty,
                                          boolean geminiConfigured, AiFallbackReason reason, RuntimeException ex) {
        String exceptionType = ex == null ? "None" : ex.getClass().getSimpleName();
        Integer httpStatusCode = extractHttpStatusCode(ex);
        String httpStatusFamily = extractHttpStatusFamily(ex);

        return "Falling back to placeholder course. reasonCategory=" + reason
                + ", topic='" + normalizedTopic + "'"
                + ", requestedDifficulty=" + requestedDifficulty
                + ", geminiConfigured=" + geminiConfigured
                + ", exceptionType=" + exceptionType
                + ", httpStatusCode=" + (httpStatusCode == null ? "None" : httpStatusCode)
                + ", httpStatusFamily=" + (httpStatusFamily == null ? "None" : httpStatusFamily);
    }

    private Integer extractHttpStatusCode(RuntimeException ex) {
        if (ex instanceof GeminiException geminiException) {
            return geminiException.getHttpStatusCode();
        }
        return null;
    }

    private String extractHttpStatusFamily(RuntimeException ex) {
        if (ex instanceof GeminiException geminiException) {
            return geminiException.getHttpStatusFamily();
        }
        return null;
    }

    private String toDisplayTitle(String normalizedTopic) {
        return List.of(normalizedTopic.split(" ")).stream()
                .filter(part -> !part.isBlank())
                .map(this::capitalize)
                .collect(Collectors.joining(" "));
    }

    private String capitalize(String input) {
        if (input.isEmpty()) {
            return input;
        }

        return input.substring(0, 1).toUpperCase(Locale.ROOT) + input.substring(1);
    }
}

enum AiFallbackReason {
    MISSING_GEMINI_CONFIG,
    GEMINI_REQUEST_FAILURE,
    EMPTY_GEMINI_RESPONSE_TEXT,
    RESPONSE_EXTRACTION_FAILURE,
    PARSER_VALIDATION_FAILURE,
    REQUESTED_DIFFICULTY_MISMATCH,
    UNEXPECTED_AI_INTEGRATION_ERROR
}
