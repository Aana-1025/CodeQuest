package com.codequest.progress;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, UUID> {
    Optional<Progress> findByUserIdAndLevelId(UUID userId, UUID levelId);

    long countByUserIdAndLevelId(UUID userId, UUID levelId);
}
