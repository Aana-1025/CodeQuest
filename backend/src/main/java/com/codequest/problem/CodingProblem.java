package com.codequest.problem;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.codequest.level.Level;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "coding_problems", indexes = {
        @Index(name = "idx_coding_problems_level_id", columnList = "level_id"),
        @Index(name = "idx_coding_problems_difficulty", columnList = "difficulty")
})
public class CodingProblem {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "starter_code_json", nullable = false)
    private Map<String, String> starterCodeJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "test_cases_json", nullable = false)
    private List<Map<String, String>> testCasesJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "hidden_tests_json", nullable = false)
    private List<Map<String, String>> hiddenTestsJson;

    @Column(nullable = false, length = 20)
    private String difficulty;

    @Column(name = "xp_reward", nullable = false)
    private Integer xpReward;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CodingProblem() {
    }

    public CodingProblem(
            UUID id,
            Level level,
            String title,
            String description,
            Map<String, String> starterCodeJson,
            List<Map<String, String>> testCasesJson,
            List<Map<String, String>> hiddenTestsJson,
            String difficulty,
            Integer xpReward,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.level = level;
        this.title = title;
        this.description = description;
        this.starterCodeJson = starterCodeJson;
        this.testCasesJson = testCasesJson;
        this.hiddenTestsJson = hiddenTestsJson;
        this.difficulty = difficulty;
        this.xpReward = xpReward;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public Level getLevel() {
        return level;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getStarterCodeJson() {
        return starterCodeJson;
    }

    public List<Map<String, String>> getTestCasesJson() {
        return testCasesJson;
    }

    public List<Map<String, String>> getHiddenTestsJson() {
        return hiddenTestsJson;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Integer getXpReward() {
        return xpReward;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
