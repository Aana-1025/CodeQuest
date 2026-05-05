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

    @InjectMocks
    private CourseService courseService;

    @Test
    void generateCourse_shouldCreatePlaceholderCourseOnCacheMiss() {
        User creator = createUser();
        GenerateCourseRequest request = new GenerateCourseRequest("Binary Search", CourseDifficulty.BEGINNER, "DSA");

        when(courseRepository.findByNormalizedTopicAndDifficulty("binary search", CourseDifficulty.BEGINNER))
                .thenReturn(Optional.empty());

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

        verify(courseRepository).findByNormalizedTopicAndDifficulty("binary search", CourseDifficulty.BEGINNER);
        verify(courseRepository).save(any(Course.class));
        verify(levelRepository).findByCourseIdOrderByOrderNumberAsc(savedCourse.getId());
    }

    @Test
    void generateCourse_shouldReturnExistingCourseOnCacheHitWithNormalizedTopic() {
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
