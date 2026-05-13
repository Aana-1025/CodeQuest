package com.codequest.flashcard;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, UUID> {
    List<Flashcard> findByLevelIdInOrderByLevelIdAscOrderNumberAsc(List<UUID> levelIds);
}
