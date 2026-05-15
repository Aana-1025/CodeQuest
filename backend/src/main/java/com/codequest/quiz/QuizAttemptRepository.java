package com.codequest.quiz;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
    long countByQuizId(UUID quizId);

    @Query("""
            select quizAttempt
            from QuizAttempt quizAttempt
            join fetch quizAttempt.quiz quiz
            join fetch quiz.level level
            join fetch level.course course
            where quizAttempt.user.id = :userId
            order by quizAttempt.attemptedAt desc
            """)
    List<QuizAttempt> findByUserIdOrderByAttemptedAtDesc(@Param("userId") UUID userId);
}
