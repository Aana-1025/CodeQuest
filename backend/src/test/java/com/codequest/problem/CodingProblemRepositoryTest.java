package com.codequest.problem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.codequest.course.Course;
import com.codequest.course.CourseDifficulty;
import com.codequest.course.CourseRepository;
import com.codequest.course.CourseSourceType;
import com.codequest.level.Level;
import com.codequest.level.LevelRepository;
import com.codequest.user.User;
import com.codequest.user.UserRank;
import com.codequest.user.UserRepository;
import com.codequest.user.UserRole;

@DataJpaTest
class CodingProblemRepositoryTest {

    @Autowired
    private CodingProblemRepository codingProblemRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFetchCodingProblemWithJsonContent() {
        Instant now = Instant.now();

        User creator = new User(UUID.randomUUID(), "Problem Creator", "problemcreator@example.com", "hashed-password");
        creator.setRank(UserRank.BEGINNER);
        creator.setRole(UserRole.STUDENT);
        creator.setXp(0);
        creator.setStreak(0);
        creator.setCreatedAt(now);
        creator.setUpdatedAt(now);
        userRepository.save(creator);

        Course course = new Course(
                UUID.randomUUID(),
                "graphs",
                "Graphs",
                "A beginner course on graphs.",
                creator,
                CourseDifficulty.BEGINNER,
                false,
                100,
                CourseSourceType.AI,
                now,
                now
        );

        Level level = new Level(
                UUID.randomUUID(),
                null,
                "Graph Warmup",
                "# Graph Warmup",
                1,
                false,
                50,
                now,
                now
        );
        course.addLevel(level);
        courseRepository.save(course);

        Level savedLevel = levelRepository.findByCourseIdOrderByOrderNumberAsc(course.getId()).get(0);
        CodingProblem codingProblem = new CodingProblem(
                UUID.randomUUID(),
                savedLevel,
                "Find Connected Nodes",
                "Return how many nodes are reachable from the start vertex.",
                Map.of(
                        "java", "public class Main {}",
                        "python", "def solve():\n    pass",
                        "javascript", "function solve() {}",
                        "cpp", "int main() { return 0; }"
                ),
                List.of(Map.of("stdin", "", "expectedOutput", "3")),
                List.of(Map.of("stdin", "", "expectedOutput", "5")),
                "MEDIUM",
                120,
                now,
                now
        );

        codingProblemRepository.save(codingProblem);

        List<CodingProblem> savedProblems = codingProblemRepository.findByLevelIdInOrderByLevelIdAscCreatedAtAsc(List.of(savedLevel.getId()));

        assertEquals(1, savedProblems.size());
        assertEquals("Find Connected Nodes", savedProblems.get(0).getTitle());
        assertEquals("MEDIUM", savedProblems.get(0).getDifficulty());
        assertEquals("public class Main {}", savedProblems.get(0).getStarterCodeJson().get("java"));
        assertEquals("3", savedProblems.get(0).getTestCasesJson().get(0).get("expectedOutput"));
        assertEquals("5", savedProblems.get(0).getHiddenTestsJson().get(0).get("expectedOutput"));
        assertFalse(savedProblems.get(0).getHiddenTestsJson().isEmpty());
        assertTrue(codingProblemRepository.countByLevel_Course_Id(course.getId()) > 0);
    }
}
