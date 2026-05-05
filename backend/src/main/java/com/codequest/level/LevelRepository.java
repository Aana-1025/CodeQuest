package com.codequest.level;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelRepository extends JpaRepository<Level, UUID> {
    List<Level> findByCourseIdOrderByOrderNumberAsc(UUID courseId);
}
