package com.codequest.course;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
import com.codequest.ai.GeminiService;
import com.codequest.ai.ResponseParser;
import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.course.dto.CourseLevelSummaryResponse;
import com.codequest.course.dto.GenerateCourseRequest;
import com.codequest.course.dto.GenerateCourseResponse;
import com.codequest.level.Level;
import com.codequest.level.LevelRepository;
import com.codequest.user.User;
import com.codequest.user.UserRepository;

@Service
public class CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;
    private final LevelRepository levelRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final ResponseParser responseParser;

    public CourseService(CourseRepository courseRepository, LevelRepository levelRepository, UserRepository userRepository,
                         GeminiService geminiService, ResponseParser responseParser) {
        this.courseRepository = courseRepository;
        this.levelRepository = levelRepository;
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

    private GenerateCourseResponse createCourseWithSafeFallback(User creator, String normalizedTopic, GenerateCourseRequest request) {
        if (geminiService.isConfigured()) {
            try {
                String rawAiResponse = geminiService.generateCourseJson(request);
                AiCourseResponse aiCourseResponse = responseParser.parseCourseResponse(rawAiResponse);
                validateRequestedDifficulty(request.difficulty(), aiCourseResponse.difficulty());
                return createAiCourse(creator, normalizedTopic, request.difficulty(), aiCourseResponse);
            } catch (GeminiException ex) {
                logger.info("Falling back to placeholder course for topic '{}' because Gemini request failed.", normalizedTopic);
            } catch (AiResponseValidationException ex) {
                logger.info("Falling back to placeholder course for topic '{}' because AI response validation failed.", normalizedTopic);
            } catch (IllegalArgumentException ex) {
                logger.info("Falling back to placeholder course for topic '{}' because AI difficulty did not match request.", normalizedTopic);
            } catch (RuntimeException ex) {
                logger.info("Falling back to placeholder course for topic '{}' because AI generation failed safely.", normalizedTopic);
            }
        }

        return createPlaceholderCourse(creator, normalizedTopic, request.difficulty());
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
