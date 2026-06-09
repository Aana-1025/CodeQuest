package com.codequest.problem;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeSubmissionRepository extends JpaRepository<CodeSubmission, UUID> {
    boolean existsByUser_IdAndProblemIdAndPassedTrue(UUID userId, UUID problemId);

    Page<CodeSubmission> findByUser_IdAndProblemId(UUID userId, UUID problemId, Pageable pageable);
}
