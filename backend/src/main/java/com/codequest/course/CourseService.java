package com.codequest.course;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final CourseRepository courseRepository;
    private final LevelRepository levelRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, LevelRepository levelRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.levelRepository = levelRepository;
        this.userRepository = userRepository;
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
                .orElseGet(() -> createPlaceholderCourse(creator, normalizedTopic, request.difficulty()));
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

    private GenerateCourseResponse toGenerateCourseResponse(Course course, boolean cacheHit) {
        List<Level> orderedLevels = levelRepository.findByCourseIdOrderByOrderNumberAsc(course.getId());
        if (orderedLevels.isEmpty()) {
            orderedLevels = course.getLevels().stream()
                    .sorted(java.util.Comparator.comparing(Level::getOrderNumber))
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
