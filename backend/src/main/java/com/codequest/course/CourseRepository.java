package com.codequest.course;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    Optional<Course> findByNormalizedTopicAndDifficulty(String normalizedTopic, CourseDifficulty difficulty);

    boolean existsByNormalizedTopicAndDifficulty(String normalizedTopic, CourseDifficulty difficulty);
}
