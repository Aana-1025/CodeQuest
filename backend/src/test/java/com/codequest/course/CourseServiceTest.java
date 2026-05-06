package com.codequest.course;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codequest.ai.AiCourseResponse;
import com.codequest.ai.AiLevelResponse;
import com.codequest.ai.AiResponseValidationException;
import com.codequest.ai.GeminiException;
import com.codequest.ai.GeminiService;
import com.codequest.ai.ResponseParser;
import com.codequest.course.dto.GenerateCourseRequest;
import com.codequest.course.dto.GenerateCourseResponse;
import com.codequest.level.Level;
import com.codequest.level.LevelRepository;
import com.codequest.user.User;
import com.codequest.user.UserRank;
import com.codequest.user.UserRole;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private GeminiService geminiService;

    @Mock
    private ResponseParser responseParser;

    @InjectMocks
    private CourseService courseService;

    @Test
    void generateCourse_shouldCreatePlaceholderCourseOnCacheMissWhenGeminiIsNotConfigured() {
        User creator = createUser();
        GenerateCourseRequest request = new GenerateCourseRequest("Binary Search", CourseDifficulty.BEGINNER, "DSA");

        when(courseRepository.findByNormalizedTopicAndDifficulty("binary search", CourseDifficulty.BEGINNER))
                .thenReturn(Optional.empty());
        when(geminiService.isConfigured()).thenReturn(false);

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        when(courseRepository.save(courseCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GenerateCourseResponse response = courseService.generateCourse(creator, request);

        Course savedCourse = courseCaptor.getValue();

        assertFalse(response.cacheHit());
        assertEquals(savedCourse.getId(), response.courseId());
        assertEquals("Binary Search", response.title());
        assertEquals("A CodeQuest course foundation for Binary Search.", response.description());
        assertEquals(3, response.levels().size());
        assertEquals(225, savedCourse.getTotalXp());
        assertEquals(CourseSourceType.PLACEHOLDER, savedCourse.getSourceType());
        assertEquals(3, savedCourse.getLevels().size());
        assertEquals(1, response.levels().get(0).orderNumber());
        assertEquals(2, response.levels().get(1).orderNumber());
        assertEquals(3, response.levels().get(2).orderNumber());
        assertTrue(response.levels().get(2).isBoss());
        assertEquals(AiFallbackReason.MISSING_GEMINI_CONFIG, courseService.determineFallbackReason(false, null));

        verify(courseRepository).findByNormalizedTopicAndDifficulty("binary search", CourseDifficulty.BEGINNER);
        verify(courseRepository).save(any(Course.class));
        verify(geminiService).isConfigured();
        verify(geminiService, never()).generateCourseJson(any());
        verify(responseParser, never()).parseCourseResponse(any());
        verify(levelRepository).findByCourseIdOrderByOrderNumberAsc(savedCourse.getId());
    }

    @Test
    void generateCourse_shouldReturnExistingCourseOnCacheHitWithNormalizedTopicWithoutCallingGemini() {
        User creator = createUser();
        Course existingCourse = createCourse(creator, "binary search", "Binary Search");
        List<Level> existingLevels = createOrderedLevels(existingCourse);
        existingCourse.setLevels(existingLevels);

        when(courseRepository.findByNormalizedTopicAndDifficulty("binary search", CourseDifficulty.BEGINNER))
                .thenReturn(Optional.of(existingCourse));
        when(levelRepository.findByCourseIdOrderByOrderNumberAsc(existingCourse.getId()))
                .thenReturn(existingLevels);

        GenerateCourseResponse response = courseService.generateCourse(
                creator,
                new GenerateCourseRequest("  BINARY   SEARCH  ", CourseDifficulty.BEGINNER, null)
        );

        assertTrue(response.cacheHit());
        assertEquals(existingCourse.getId(), response.courseId());
        assertEquals(3, response.levels().size());

        verify(courseRepository).findByNormalizedTopicAndDifficulty("binary search", CourseDifficulty.BEGINNER);
        verify(courseRepository, never()).save(any(Course.class));
        verify(geminiService, never()).isConfigured();
        verify(geminiService, never()).generateCourseJson(any());
        verify(responseParser, never()).parseCourseResponse(any());
        verify(levelRepository).findByCourseIdOrderByOrderNumberAsc(existingCourse.getId());
    }

    @Test
    void generateCourse_shouldReturnLevelsOrderedByOrderNumberAscending() {
        User creator = createUser();
        Course existingCourse = createCourse(creator, "java basics", "Java Basics");

        Level third = createLevel(existingCourse, "Java Basics Boss Challenge", 3, true, 100);
        Level first = createLevel(existingCourse, "Introduction to Java Basics", 1, false, 50);
        Level second = createLevel(existingCourse, "Practice Java Basics", 2, false, 75);

        when(courseRepository.findByNormalizedTopicAndDifficulty("java basics", CourseDifficulty.BEGINNER))
                .thenReturn(Optional.of(existingCourse));
        when(levelRepository.findByCourseIdOrderByOrderNumberAsc(existingCourse.getId()))
                .thenReturn(List.of(first, second, third));

        GenerateCourseResponse response = courseService.generateCourse(
                creator,
                new GenerateCourseRequest("Java Basics", CourseDifficulty.BEGINNER, null)
        );

        assertEquals(3, response.levels().size());
        assertEquals(1, response.levels().get(0).orderNumber());
        assertEquals("Introduction to Java Basics", response.levels().get(0).title());
        assertEquals(2, response.levels().get(1).orderNumber());
        assertEquals(3, response.levels().get(2).orderNumber());
        assertTrue(response.levels().get(2).isBoss());
        verify(geminiService, never()).isConfigured();
    }

    @Test
    void generateCourse_shouldFallbackToPlaceholderWhenGeminiCallFails() {
        User creator = createUser();
        GenerateCourseRequest request = new GenerateCourseRequest("Binary Search", CourseDifficulty.BEGINNER, "DSA");

        when(courseRepository.findByNormalizedTopicAndDifficulty("binary search", CourseDifficulty.BEGINNER))
                .thenReturn(Optional.empty());
        when(geminiService.isConfigured()).thenReturn(true);
        when(geminiService.generateCourseJson(request))
                .thenThrow(new GeminiException(GeminiException.Category.REQUEST_FAILURE, "Gemini request failed."));

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        when(courseRepository.save(courseCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GenerateCourseResponse response = courseService.generateCourse(creator, request);

        assertFalse(response.cacheHit());
        assertEquals("Binary Search", response.title());
        assertEquals(CourseSourceType.PLACEHOLDER, courseCaptor.getValue().getSourceType());
        assertEquals(3, response.levels().size());
        assertEquals(
                AiFallbackReason.GEMINI_REQUEST_FAILURE,
                courseService.determineFallbackReason(true, new GeminiException(GeminiException.Category.REQUEST_FAILURE, "Gemini request failed."))
        );

        verify(responseParser, never()).parseCourseResponse(any());
    }

    @Test
    void generateCourse_shouldFallbackToPlaceholderWhenAiResponseIsInvalid() {
        User creator = createUser();
        GenerateCourseRequest request = new GenerateCourseRequest("Binary Search", CourseDifficulty.BEGINNER, "DSA");

        when(courseRepository.findByNormalizedTopicAndDifficulty("binary search", CourseDifficulty.BEGINNER))
                .thenReturn(Optional.empty());
        when(geminiService.isConfigured()).thenReturn(true);
        when(geminiService.generateCourseJson(request)).thenReturn("{\"invalid\":true}");
        when(responseParser.parseCourseResponse("{\"invalid\":true}"))
                .thenThrow(new AiResponseValidationException("title is required."));

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        when(courseRepository.save(courseCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GenerateCourseResponse response = courseService.generateCourse(creator, request);

        assertFalse(response.cacheHit());
        assertEquals(CourseSourceType.PLACEHOLDER, courseCaptor.getValue().getSourceType());
        assertEquals(3, response.levels().size());
        assertEquals(
                AiFallbackReason.PARSER_VALIDATION_FAILURE,
                courseService.determineFallbackReason(true, new AiResponseValidationException("title is required."))
        );
    }

    @Test
    void generateCourse_shouldCreateAiCourseWhenGeminiAndParserSucceed() {
        User creator = createUser();
        GenerateCourseRequest request = new GenerateCourseRequest("Graph Theory", CourseDifficulty.INTERMEDIATE, "Interview prep");

        when(courseRepository.findByNormalizedTopicAndDifficulty("graph theory", CourseDifficulty.INTERMEDIATE))
                .thenReturn(Optional.empty());
        when(geminiService.isConfigured()).thenReturn(true);
        when(geminiService.generateCourseJson(request)).thenReturn("{\"title\":\"Graph Theory\"}");
        when(responseParser.parseCourseResponse("{\"title\":\"Graph Theory\"}"))
                .thenReturn(new AiCourseResponse(
                        "Graph Theory",
                        "A structured course on graph theory for interviews.",
                        "INTERMEDIATE",
                        List.of(
                                new AiLevelResponse(
                                        "Graph Basics",
                                        "# Graph Basics\n\nLearn vertices, edges, and representations.",
                                        2,
                                        false,
                                        80,
                                        List.of(),
                                        List.of(),
                                        List.of()
                                ),
                                new AiLevelResponse(
                                        "Graph Traversal Boss",
                                        "# Graph Traversal Boss\n\nSolve BFS and DFS challenge problems.",
                                        3,
                                        true,
                                        120,
                                        List.of(),
                                        List.of(),
                                        List.of()
                                ),
                                new AiLevelResponse(
                                        "Introduction to Graphs",
                                        "# Introduction to Graphs\n\nBuild intuition for graph problems first.",
                                        1,
                                        false,
                                        60,
                                        List.of(),
                                        List.of(),
                                        List.of()
                                )
                        )
                ));

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        when(courseRepository.save(courseCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GenerateCourseResponse response = courseService.generateCourse(creator, request);
        Course savedCourse = courseCaptor.getValue();

        assertFalse(response.cacheHit());
        assertEquals("Graph Theory", response.title());
        assertEquals("A structured course on graph theory for interviews.", response.description());
        assertEquals(CourseSourceType.AI, savedCourse.getSourceType());
        assertEquals(260, savedCourse.getTotalXp());
        assertEquals(3, savedCourse.getLevels().size());
        assertEquals(1, response.levels().get(0).orderNumber());
        assertEquals("Introduction to Graphs", response.levels().get(0).title());
        assertEquals(2, response.levels().get(1).orderNumber());
        assertEquals(3, response.levels().get(2).orderNumber());
        assertTrue(response.levels().get(2).isBoss());
    }

    @Test
    void generateCourse_shouldFallbackToPlaceholderWhenAiDifficultyDoesNotMatchRequest() {
        User creator = createUser();
        GenerateCourseRequest request = new GenerateCourseRequest("Binary Search", CourseDifficulty.BEGINNER, "DSA");

        when(courseRepository.findByNormalizedTopicAndDifficulty("binary search", CourseDifficulty.BEGINNER))
                .thenReturn(Optional.empty());
        when(geminiService.isConfigured()).thenReturn(true);
        when(geminiService.generateCourseJson(request)).thenReturn("{\"title\":\"Binary Search\"}");
        when(responseParser.parseCourseResponse("{\"title\":\"Binary Search\"}"))
                .thenReturn(new AiCourseResponse(
                        "Binary Search",
                        "A beginner-friendly course on binary search concepts.",
                        "ADVANCED",
                        List.of(
                                new AiLevelResponse(
                                        "Introduction to Binary Search",
                                        "# Introduction to Binary Search\n\nUnderstand the sorted search space well.",
                                        1,
                                        false,
                                        50,
                                        List.of(),
                                        List.of(),
                                        List.of()
                                )
                        )
                ));

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        when(courseRepository.save(courseCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GenerateCourseResponse response = courseService.generateCourse(creator, request);

        assertFalse(response.cacheHit());
        assertEquals(CourseSourceType.PLACEHOLDER, courseCaptor.getValue().getSourceType());
        assertEquals(3, response.levels().size());
        assertEquals(
                AiFallbackReason.REQUESTED_DIFFICULTY_MISMATCH,
                courseService.determineFallbackReason(true, new IllegalArgumentException("AI difficulty did not match the requested difficulty."))
        );
    }

    private User createUser() {
        Instant now = Instant.now();
        User user = new User(UUID.randomUUID(), "Course Creator", "creator@example.com", "hashed-password");
        user.setRank(UserRank.BEGINNER);
        user.setRole(UserRole.STUDENT);
        user.setXp(0);
        user.setStreak(0);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }

    private Course createCourse(User creator, String normalizedTopic, String title) {
        Instant now = Instant.now();
        return new Course(
                UUID.randomUUID(),
                normalizedTopic,
                title,
                "A CodeQuest course foundation for " + title + ".",
                creator,
                CourseDifficulty.BEGINNER,
                false,
                225,
                CourseSourceType.PLACEHOLDER,
                now,
                now
        );
    }

    private List<Level> createOrderedLevels(Course course) {
        return List.of(
                createLevel(course, "Introduction to Binary Search", 1, false, 50),
                createLevel(course, "Practice Binary Search", 2, false, 75),
                createLevel(course, "Binary Search Boss Challenge", 3, true, 100)
        );
    }

    private Level createLevel(Course course, String title, int orderNumber, boolean isBoss, int xpReward) {
        Instant now = Instant.now();
        return new Level(
                UUID.randomUUID(),
                course,
                title,
                "# " + title,
                orderNumber,
                isBoss,
                xpReward,
                now,
                now
        );
    }
}
