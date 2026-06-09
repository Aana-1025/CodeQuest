package com.codequest.problem;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeSubmissionRepository extends JpaRepository<CodeSubmission, UUID> {
    boolean existsByUser_IdAndProblemIdAndPassedTrue(UUID userId, UUID problemId);
}
