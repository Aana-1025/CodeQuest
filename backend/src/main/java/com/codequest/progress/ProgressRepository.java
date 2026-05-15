package com.codequest.progress;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, UUID> {
    Optional<Progress> findByUserIdAndLevelId(UUID userId, UUID levelId);

    long countByUserIdAndLevelId(UUID userId, UUID levelId);

    @Query("""
            select count(p)
            from Progress p
            where p.user.id = :userId
              and p.completed = true
              and p.level.course.id = :courseId
              and p.level.orderNumber < :orderNumber
            """)
    long countCompletedLevelsBeforeOrderNumber(
            @Param("userId") UUID userId,
            @Param("courseId") UUID courseId,
            @Param("orderNumber") Integer orderNumber
    );
}
