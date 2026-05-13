package com.codequest.course;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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
import com.codequest.ai.AiFlashcardResponse;
import com.codequest.ai.AiLevelResponse;
import com.codequest.ai.AiResponseValidationException;
import com.codequest.ai.GeminiException;
import com.codequest.ai.GeminiService;
import com.codequest.ai.ResponseParser;
import com.codequest.common.exception.ApiException;
import com.codequest.common.exception.ErrorCode;
import com.codequest.course.dto.CourseResponse;
import com.codequest.course.dto.GenerateCourseRequest;
import com.codequest.course.dto.GenerateCourseResponse;
import com.codequest.flashcard.Flashcard;
import com.codequest.flashcard.FlashcardRepository;
import com.codequest.level.Level;
import com.codequest.level.LevelRepository;
import com.codequest.quiz.Quiz;
import com.codequest.quiz.QuizRepository;
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
    private QuizRepository quizRepository;

    @Mock
    private FlashcardRepository flashcardRepository;

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
        assertEquals(CourseSourceType.PLACEHOLDER, response.sourceType());
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
        verify(quizRepository, never()).saveAll(any());
        verify(flashcardRepository, never()).saveAll(any());
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
        assertEquals(CourseSourceType.PLACEHOLDER, response.sourceType());
        assertEquals(3, response.levels().size());

        verify(courseRepository).findByNormalizedTopicAndDifficulty("binary search", CourseDifficulty.BEGINNER);
        verify(courseRepository, never()).save(any(Course.class));
        verify(geminiService, never()).isConfigured();
        verify(geminiService, never()).generateCourseJson(any());
        verify(responseParser, never()).parseCourseResponse(any());
        verify(quizRepository, never()).saveAll(any());
        verify(flashcardRepository, never()).saveAll(any());
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
        assertEquals(
                "Falling back to placeholder course. reasonCategory=GEMINI_REQUEST_FAILURE, topic='binary search', requestedDifficulty=BEGINNER, geminiConfigured=true, exceptionType=GeminiException, httpStatusCode=403, httpStatusFamily=4xx",
                courseService.buildFallbackDiagnosticMessage(
                        "binary search",
                        CourseDifficulty.BEGINNER,
                        true,
                        AiFallbackReason.GEMINI_REQUEST_FAILURE,
                        new GeminiException(GeminiException.Category.REQUEST_FAILURE, "Gemini request failed.", 403, null)
                )
        );

        verify(responseParser, never()).parseCourseResponse(any());
    }

    @Test
    void generateCourse_shouldRetryOnceWhenFirstGeminiAttemptReturns503AndSecondSucceeds() {
        User creator = createUser();
        GenerateCourseRequest request = new GenerateCourseRequest("Graph DFS Gemini Retry Test", CourseDifficulty.BEGINNER, "Learn DFS");

        when(courseRepository.findByNormalizedTopicAndDifficulty("graph dfs gemini retry test", CourseDifficulty.BEGINNER))
                .thenReturn(Optional.empty());
        when(geminiService.isConfigured()).thenReturn(true);
        when(geminiService.generateCourseJson(request))
                .thenThrow(new GeminiException(GeminiException.Category.REQUEST_FAILURE, "Gemini request failed.", 503, null))
                .thenReturn("{\"title\":\"Graph DFS\"}");
        when(responseParser.parseCourseResponse("{\"title\":\"Graph DFS\"}"))
                .thenReturn(new AiCourseResponse(
                        "Graph DFS",
                        "A structured DFS course for Java interview practice.",
                        "BEGINNER",
                        List.of(
                                new AiLevelResponse(
                                        "DFS Basics",
                                        "# DFS Basics\n\nUnderstand recursion, stacks, and traversal order.",
                                        1,
                                        false,
                                        70,
                                        List.of(
                                                new AiFlashcardResponse(
                                                        "DFS uses which core data structure idea?",
                                                        "A stack, either explicit or recursive."
                                                )
                                        ),
                                        List.of(
                                                new com.codequest.ai.AiQuizQuestionResponse(
                                                        "Which traversal uses a stack-friendly approach?",
                                                        "Breadth-first search",
                                                        "Depth-first search",
                                                        "Binary search",
                                                        "Merge sort",
                                                        "B",
                                                        "DFS naturally maps to stack-based traversal.",
                                                        "dfs-fundamentals",
                                                        20
                                                )
                                        ),
                                        List.of()
                                )
                        )
                ));

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        ArgumentCaptor<List<Quiz>> quizCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Flashcard>> flashcardCaptor = ArgumentCaptor.forClass(List.class);
        when(courseRepository.save(courseCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(quizRepository.saveAll(quizCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(flashcardRepository.saveAll(flashcardCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GenerateCourseResponse response = courseService.generateCourse(creator, request);

        assertFalse(response.cacheHit());
        assertEquals(CourseSourceType.AI, response.sourceType());
        assertEquals(CourseSourceType.AI, courseCaptor.getValue().getSourceType());
        assertEquals(1, quizCaptor.getValue().size());
        assertEquals(1, flashcardCaptor.getValue().size());
        assertEquals("Which traversal uses a stack-friendly approach?", quizCaptor.getValue().get(0).getQuestion());
        assertEquals(courseCaptor.getValue().getLevels().get(0).getId(), quizCaptor.getValue().get(0).getLevel().getId());
        assertEquals("DFS uses which core data structure idea?", flashcardCaptor.getValue().get(0).getFront());
        assertEquals(courseCaptor.getValue().getLevels().get(0).getId(), flashcardCaptor.getValue().get(0).getLevel().getId());
        verify(geminiService, times(2)).generateCourseJson(request);
        verify(responseParser).parseCourseResponse("{\"title\":\"Graph DFS\"}");
    }

    @Test
    void generateCourse_shouldFallbackToPlaceholderAfterTwoGemini5xxFailures() {
        User creator = createUser();
        GenerateCourseRequest request = new GenerateCourseRequest("Binary Search", CourseDifficulty.BEGINNER, "DSA");

        when(courseRepository.findByNormalizedTopicAndDifficulty("binary search", CourseDifficulty.BEGINNER))
                .thenReturn(Optional.empty());
        when(geminiService.isConfigured()).thenReturn(true);
        when(geminiService.generateCourseJson(request))
                .thenThrow(new GeminiException(GeminiException.Category.REQUEST_FAILURE, "Gemini request failed.", 503, null))
                .thenThrow(new GeminiException(GeminiException.Category.REQUEST_FAILURE, "Gemini request failed.", 502, null));

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        when(courseRepository.save(courseCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GenerateCourseResponse response = courseService.generateCourse(creator, request);

        assertFalse(response.cacheHit());
        assertEquals(CourseSourceType.PLACEHOLDER, courseCaptor.getValue().getSourceType());
        verify(geminiService, times(2)).generateCourseJson(request);
        verify(responseParser, never()).parseCourseResponse(any());
        verify(quizRepository, never()).saveAll(any());
        verify(flashcardRepository, never()).saveAll(any());
    }

    @Test
    void generateCourse_shouldNotRetryWhenGeminiReturns403() {
        User creator = createUser();
        GenerateCourseRequest request = new GenerateCourseRequest("Binary Search", CourseDifficulty.BEGINNER, "DSA");

        when(courseRepository.findByNormalizedTopicAndDifficulty("binary search", CourseDifficulty.BEGINNER))
                .thenReturn(Optional.empty());
        when(geminiService.isConfigured()).thenReturn(true);
        when(geminiService.generateCourseJson(request))
                .thenThrow(new GeminiException(GeminiException.Category.REQUEST_FAILURE, "Gemini request failed.", 403, null));

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        when(courseRepository.save(courseCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GenerateCourseResponse response = courseService.generateCourse(creator, request);

        assertFalse(response.cacheHit());
        assertEquals(CourseSourceType.PLACEHOLDER, courseCaptor.getValue().getSourceType());
        verify(geminiService, times(1)).generateCourseJson(request);
        verify(responseParser, never()).parseCourseResponse(any());
        verify(quizRepository, never()).saveAll(any());
        verify(flashcardRepository, never()).saveAll(any());
    }

    @Test
    void generateCourse_shouldNotRetryWhenGeminiReturns429() {
        User creator = createUser();
        GenerateCourseRequest request = new GenerateCourseRequest("Binary Search", CourseDifficulty.BEGINNER, "DSA");

        when(courseRepository.findByNormalizedTopicAndDifficulty("binary search", CourseDifficulty.BEGINNER))
                .thenReturn(Optional.empty());
        when(geminiService.isConfigured()).thenReturn(true);
        when(geminiService.generateCourseJson(request))
                .thenThrow(new GeminiException(GeminiException.Category.REQUEST_FAILURE, "Gemini request failed.", 429, null));

        ArgumentCaptor<Course> courseCaptor = ArgumentCaptor.forClass(Course.class);
        when(courseRepository.save(courseCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GenerateCourseResponse response = courseService.generateCourse(creator, request);

        assertFalse(response.cacheHit());
        assertEquals(CourseSourceType.PLACEHOLDER, courseCaptor.getValue().getSourceType());
        verify(geminiService, times(1)).generateCourseJson(request);
        verify(responseParser, never()).parseCourseResponse(any());
        verify(quizRepository, never()).saveAll(any());
        verify(flashcardRepository, never()).saveAll(any());
    }

    @Test
    void buildFallbackDiagnosticMessage_shouldUseNoneWhenHttpStatusIsUnavailable() {
        assertEquals(
                "Falling back to placeholder course. reasonCategory=EMPTY_GEMINI_RESPONSE_TEXT, topic='hashmap', requestedDifficulty=BEGINNER, geminiConfigured=true, exceptionType=GeminiException, httpStatusCode=None, httpStatusFamily=None",
                courseService.buildFallbackDiagnosticMessage(
                        "hashmap",
                        CourseDifficulty.BEGINNER,
                        true,
                        AiFallbackReason.EMPTY_GEMINI_RESPONSE_TEXT,
                        new GeminiException(GeminiException.Category.EMPTY_RESPONSE_TEXT, "Gemini response did not contain usable text.")
                )
        );
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
        verify(geminiService, times(1)).generateCourseJson(request);
        verify(quizRepository, never()).saveAll(any());
        verify(flashcardRepository, never()).saveAll(any());
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
                                        List.of(
                                                new AiFlashcardResponse(
                                                        "Graph edge",
                                                        "A connection between two vertices."
                                                )
                                        ),
                                        List.of(
                                                new com.codequest.ai.AiQuizQuestionResponse(
                                                        "What best describes a graph edge?",
                                                        "A sorting rule",
                                                        "A connection between vertices",
                                                        "A loop counter",
                                                        "A stack frame",
                                                        "B",
                                                        "Edges connect two vertices in a graph.",
                                                        "graph-basics",
                                                        20
                                                )
                                        ),
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
        when(quizRepository.saveAll(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(flashcardRepository.saveAll(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        GenerateCourseResponse response = courseService.generateCourse(creator, request);
        Course savedCourse = courseCaptor.getValue();

        assertFalse(response.cacheHit());
        assertEquals("Graph Theory", response.title());
        assertEquals("A structured course on graph theory for interviews.", response.description());
        assertEquals(CourseSourceType.AI, response.sourceType());
        assertEquals(CourseSourceType.AI, savedCourse.getSourceType());
        assertEquals(260, savedCourse.getTotalXp());
        assertEquals(3, savedCourse.getLevels().size());
        assertEquals(1, response.levels().get(0).orderNumber());
        assertEquals("Introduction to Graphs", response.levels().get(0).title());
        assertEquals(2, response.levels().get(1).orderNumber());
        assertEquals(3, response.levels().get(2).orderNumber());
        assertTrue(response.levels().get(2).isBoss());
        verify(quizRepository).saveAll(any());
        verify(flashcardRepository).saveAll(any());
    }

    @Test
    void buildRetryDiagnosticMessage_shouldIncludeSafeAttemptMetadata() {
        assertEquals(
                "Retrying Gemini course generation after transient failure. topic='binary search', requestedDifficulty=BEGINNER, attempt=1, exceptionType=GeminiException, httpStatusCode=503, httpStatusFamily=5xx",
                courseService.buildRetryDiagnosticMessage(
                        " Binary Search ",
                        CourseDifficulty.BEGINNER,
                        new GeminiException(GeminiException.Category.REQUEST_FAILURE, "Gemini request failed.", 503, null),
                        1
                )
        );
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
        verify(quizRepository, never()).saveAll(any());
        verify(flashcardRepository, never()).saveAll(any());
    }

    @Test
    void getCourseById_shouldReturnCourseWithOrderedLevelsWithoutCallingGemini() {
        User creator = createUser();
        Course course = new Course(
                UUID.randomUUID(),
                "graph theory",
                "Graph Theory",
                "A structured course on graph theory.",
                creator,
                CourseDifficulty.INTERMEDIATE,
                false,
                260,
                CourseSourceType.AI,
                Instant.now(),
                Instant.now()
        );

        Level third = createLevel(course, "Graph Traversal Boss", 3, true, 120);
        Level first = createLevel(course, "Introduction to Graphs", 1, false, 60);
        Level second = createLevel(course, "Graph Basics", 2, false, 80);

        Quiz secondLevelQuiz = createQuiz(second, 1, "What is an adjacency list?", "A queue", "A graph representation", "A hash collision", "A binary tree", "B");
        Quiz thirdLevelQuiz = createQuiz(third, 1, "Which traversal can use a queue?", "DFS", "BFS", "Merge sort", "Heapify", "B");
        Flashcard firstLevelFlashcard = createFlashcard(first, 2, "Graph traversal start", "Choose a node and mark it visited.");
        Flashcard secondLevelFlashcard = createFlashcard(second, 1, "Adjacency list", "Stores each vertex with its connected neighbors.");

        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(levelRepository.findByCourseIdOrderByOrderNumberAsc(course.getId())).thenReturn(List.of(first, second, third));
        when(quizRepository.findByLevelIdInOrderByLevelIdAscOrderNumberAsc(List.of(first.getId(), second.getId(), third.getId())))
                .thenReturn(List.of(secondLevelQuiz, thirdLevelQuiz));
        when(flashcardRepository.findByLevelIdInOrderByLevelIdAscOrderNumberAsc(List.of(first.getId(), second.getId(), third.getId())))
                .thenReturn(List.of(firstLevelFlashcard, secondLevelFlashcard));

        CourseResponse response = courseService.getCourseById(course.getId());

        assertEquals(course.getId(), response.courseId());
        assertEquals("Graph Theory", response.title());
        assertEquals(CourseDifficulty.INTERMEDIATE, response.difficulty());
        assertEquals(CourseSourceType.AI, response.sourceType());
        assertEquals(260, response.totalXp());
        assertEquals(3, response.levels().size());
        assertEquals(1, response.levels().get(0).orderNumber());
        assertEquals("Introduction to Graphs", response.levels().get(0).title());
        assertEquals("# Introduction to Graphs", response.levels().get(0).contentMarkdown());
        assertEquals(0, response.levels().get(0).quizQuestions().size());
        assertEquals(1, response.levels().get(0).flashcards().size());
        assertEquals("Graph traversal start", response.levels().get(0).flashcards().get(0).front());
        assertEquals(1, response.levels().get(1).quizQuestions().size());
        assertEquals("What is an adjacency list?", response.levels().get(1).quizQuestions().get(0).question());
        assertEquals("A graph representation", response.levels().get(1).quizQuestions().get(0).options().b());
        assertEquals("graph-basics", response.levels().get(1).quizQuestions().get(0).conceptTag());
        assertEquals(1, response.levels().get(1).flashcards().size());
        assertEquals("Adjacency list", response.levels().get(1).flashcards().get(0).front());
        assertEquals(3, response.levels().get(2).orderNumber());
        assertTrue(response.levels().get(2).isBoss());

        verify(geminiService, never()).isConfigured();
        verify(geminiService, never()).generateCourseJson(any());
        verify(responseParser, never()).parseCourseResponse(any());
    }

    @Test
    void getCourseById_shouldReturnEmptyQuizQuestionsWhenNoQuizzesExist() {
        User creator = createUser();
        Course course = createCourse(creator, "binary search", "Binary Search");
        List<Level> levels = createOrderedLevels(course);

        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(levelRepository.findByCourseIdOrderByOrderNumberAsc(course.getId())).thenReturn(levels);
        when(quizRepository.findByLevelIdInOrderByLevelIdAscOrderNumberAsc(List.of(
                levels.get(0).getId(),
                levels.get(1).getId(),
                levels.get(2).getId()
        ))).thenReturn(List.of());
        when(flashcardRepository.findByLevelIdInOrderByLevelIdAscOrderNumberAsc(List.of(
                levels.get(0).getId(),
                levels.get(1).getId(),
                levels.get(2).getId()
        ))).thenReturn(List.of());

        CourseResponse response = courseService.getCourseById(course.getId());

        assertEquals(3, response.levels().size());
        assertTrue(response.levels().stream().allMatch(level -> level.quizQuestions().isEmpty()));
        assertTrue(response.levels().stream().allMatch(level -> level.flashcards().isEmpty()));
        verify(geminiService, never()).generateCourseJson(any());
    }

    @Test
    void getCourseById_shouldThrowNotFoundWhenCourseMissing() {
        UUID courseId = UUID.randomUUID();
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> courseService.getCourseById(courseId));

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        assertEquals("Course not found.", exception.getMessage());
        verify(levelRepository, never()).findByCourseIdOrderByOrderNumberAsc(any());
        verify(quizRepository, never()).findByLevelIdInOrderByLevelIdAscOrderNumberAsc(any());
        verify(flashcardRepository, never()).findByLevelIdInOrderByLevelIdAscOrderNumberAsc(any());
        verify(geminiService, never()).generateCourseJson(any());
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

    private Quiz createQuiz(Level level, int orderNumber, String question, String optionA, String optionB,
                            String optionC, String optionD, String correctAnswer) {
        Instant now = Instant.now();
        return new Quiz(
                UUID.randomUUID(),
                level,
                orderNumber,
                question,
                optionA,
                optionB,
                optionC,
                optionD,
                correctAnswer,
                "Explanation for " + question,
                "graph-basics",
                20,
                now,
                now
        );
    }

    private Flashcard createFlashcard(Level level, int orderNumber, String front, String back) {
        Instant now = Instant.now();
        return new Flashcard(
                UUID.randomUUID(),
                level,
                orderNumber,
                front,
                back,
                "graph-basics",
                now,
                now
        );
    }
}
