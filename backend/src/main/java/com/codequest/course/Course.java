package com.codequest.course;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.codequest.level.Level;
import com.codequest.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses", indexes = {
        @Index(name = "ux_courses_normalized_topic_difficulty", columnList = "normalized_topic,difficulty", unique = true),
        @Index(name = "idx_courses_normalized_topic", columnList = "normalized_topic"),
        @Index(name = "idx_courses_created_by", columnList = "created_by")
})
public class Course {

    @Id
    private UUID id;

    @Column(name = "normalized_topic", nullable = false, length = 120)
    private String normalizedTopic;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private CourseDifficulty difficulty;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "total_xp", nullable = false)
    private Integer totalXp = 0;

    @Column(name = "source_type", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private CourseSourceType sourceType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNumber ASC")
    private List<Level> levels = new ArrayList<>();

    protected Course() {
    }

    public Course(UUID id, String normalizedTopic, String title, String description, User createdBy,
                  CourseDifficulty difficulty, boolean isPublic, Integer totalXp, CourseSourceType sourceType,
                  Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.normalizedTopic = normalizedTopic;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.difficulty = difficulty;
        this.isPublic = isPublic;
        this.totalXp = totalXp;
        this.sourceType = sourceType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void addLevel(Level level) {
        levels.add(level);
        level.setCourse(this);
    }

    public void removeLevel(Level level) {
        levels.remove(level);
        level.setCourse(null);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNormalizedTopic() {
        return normalizedTopic;
    }

    public void setNormalizedTopic(String normalizedTopic) {
        this.normalizedTopic = normalizedTopic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public CourseDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(CourseDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public Integer getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(Integer totalXp) {
        this.totalXp = totalXp;
    }

    public CourseSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(CourseSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Level> getLevels() {
        return levels;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }
}
