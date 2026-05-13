package com.codequest.note;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {
    Optional<Note> findByUserIdAndLevelId(UUID userId, UUID levelId);

    long countByUserIdAndLevelId(UUID userId, UUID levelId);
}
