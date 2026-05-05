package com.codequest.course;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.codequest.level.Level;
import com.codequest.level.LevelRepository;
import com.codequest.user.User;
import com.codequest.user.UserRank;
import com.codequest.user.UserRepository;
import com.codequest.user.UserRole;

@DataJpaTest
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveCourseWithLevelsAndFindByNormalizedTopicAndDifficulty() {
        Instant now = Instant.now();

        User creator = new User(UUID.randomUUID(), "Course Creator", "creator@example.com", "hashed-password");
        creator.setRank(UserRank.BEGINNER);
        creator.setRole(UserRole.STUDENT);
        creator.setXp(0);
        creator.setStreak(0);
        creator.setCreatedAt(now);
        creator.setUpdatedAt(now);
        userRepository.save(creator);

        Course course = new Course(
                UUID.randomUUID(),
                "binary search",
                "Binary Search",
                "A beginner course on binary search.",
                creator,
                CourseDifficulty.BEGINNER,
                false,
                100,
                CourseSourceType.PLACEHOLDER,
                now,
                now
        );

        Level secondLevel = new Level(
                UUID.randomUUID(),
                null,
                "Practice Binary Search",
                "Practice with sorted arrays.",
                2,
                false,
                50,
                now,
                now
        );
        Level firstLevel = new Level(
                UUID.randomUUID(),
                null,
                "What is Binary Search?",
                "Binary search works on sorted data.",
                1,
                false,
                50,
                now,
                now
        );

        course.addLevel(secondLevel);
        course.addLevel(firstLevel);

        courseRepository.save(course);

        Optional<Course> foundCourse = courseRepository.findByNormalizedTopicAndDifficulty(
                "binary search",
                CourseDifficulty.BEGINNER
        );

        assertTrue(foundCourse.isPresent());
        assertEquals("Binary Search", foundCourse.get().getTitle());
        assertEquals(2, foundCourse.get().getLevels().size());

        List<Level> orderedLevels = levelRepository.findByCourseIdOrderByOrderNumberAsc(course.getId());

        assertEquals(2, orderedLevels.size());
        assertEquals(1, orderedLevels.get(0).getOrderNumber());
        assertEquals("What is Binary Search?", orderedLevels.get(0).getTitle());
        assertEquals(2, orderedLevels.get(1).getOrderNumber());
        assertEquals("Practice Binary Search", orderedLevels.get(1).getTitle());
    }
}
