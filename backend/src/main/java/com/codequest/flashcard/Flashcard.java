package com.codequest.flashcard;

import java.time.Instant;
import java.util.UUID;

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
@Table(name = "flashcards", indexes = {
        @Index(name = "ux_flashcards_level_id_order_number", columnList = "level_id,order_number", unique = true),
        @Index(name = "idx_flashcards_level_id", columnList = "level_id")
})
public class Flashcard {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String front;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String back;

    @Column(name = "concept_tag", length = 120)
    private String conceptTag;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Flashcard() {
    }

    public Flashcard(UUID id, Level level, Integer orderNumber, String front, String back, String conceptTag,
                     Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.level = level;
        this.orderNumber = orderNumber;
        this.front = front;
        this.back = back;
        this.conceptTag = conceptTag;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getFront() {
        return front;
    }

    public void setFront(String front) {
        this.front = front;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public String getConceptTag() {
        return conceptTag;
    }

    public void setConceptTag(String conceptTag) {
        this.conceptTag = conceptTag;
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
}
