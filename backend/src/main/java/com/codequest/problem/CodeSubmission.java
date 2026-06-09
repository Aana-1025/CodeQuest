package com.codequest.problem;

import java.time.Instant;
import java.util.UUID;

import com.codequest.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "code_submissions", indexes = {
        @Index(name = "idx_code_submissions_user_id", columnList = "user_id"),
        @Index(name = "idx_code_submissions_user_id_problem_id", columnList = "user_id, problem_id"),
        @Index(name = "idx_code_submissions_problem_id", columnList = "problem_id"),
        @Index(name = "idx_code_submissions_submitted_at", columnList = "submitted_at")
})
public class CodeSubmission {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "problem_id", nullable = false)
    private UUID problemId;

    @Column(nullable = false, length = 20)
    private String language;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(nullable = false)
    private boolean passed;

    @Column(name = "passed_test_cases", nullable = false)
    private Integer passedTestCases;

    @Column(name = "total_test_cases", nullable = false)
    private Integer totalTestCases;

    @Column(name = "runtime_ms")
    private Integer runtimeMs;

    @Column(name = "memory_kb")
    private Integer memoryKb;

    @Column(name = "ai_review", columnDefinition = "TEXT")
    private String aiReview;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CodeSubmission() {
    }

    public CodeSubmission(
            UUID id,
            User user,
            UUID problemId,
            String language,
            String code,
            boolean passed,
            Integer passedTestCases,
            Integer totalTestCases,
            Integer runtimeMs,
            Integer memoryKb,
            String aiReview,
            Instant submittedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.user = user;
        this.problemId = problemId;
        this.language = language;
        this.code = code;
        this.passed = passed;
        this.passedTestCases = passedTestCases;
        this.totalTestCases = totalTestCases;
        this.runtimeMs = runtimeMs;
        this.memoryKb = memoryKb;
        this.aiReview = aiReview;
        this.submittedAt = submittedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public UUID getProblemId() {
        return problemId;
    }

    public String getLanguage() {
        return language;
    }

    public String getCode() {
        return code;
    }

    public boolean isPassed() {
        return passed;
    }

    public Integer getPassedTestCases() {
        return passedTestCases;
    }

    public Integer getTotalTestCases() {
        return totalTestCases;
    }

    public Integer getRuntimeMs() {
        return runtimeMs;
    }

    public Integer getMemoryKb() {
        return memoryKb;
    }

    public String getAiReview() {
        return aiReview;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
