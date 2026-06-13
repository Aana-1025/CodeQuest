package com.codequest.problem;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CodingProblemRepository extends JpaRepository<CodingProblem, UUID> {

    @Query("""
            SELECT codingProblem
            FROM CodingProblem codingProblem
            WHERE codingProblem.level.id IN :levelIds
            ORDER BY codingProblem.level.id ASC, codingProblem.createdAt ASC, codingProblem.id ASC
            """)
    List<CodingProblem> findByLevelIdInOrderByLevelIdAscCreatedAtAsc(@Param("levelIds") List<UUID> levelIds);

    long countByLevel_Course_Id(UUID courseId);
}
