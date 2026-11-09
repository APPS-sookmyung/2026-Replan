package com.yuan.replan.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
public class Diary {
    // Nullable for legacy records only.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    public void setUser(User user) { this.user = java.util.Objects.requireNonNull(user); }
    @JsonIgnore
    public User getUser() { return user; }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime createdAt;

    protected Diary() {
    }

    public Diary(String content) {
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {return createdAt;}

    public void setContent(String content) {
        this.content = content;
    }
}
