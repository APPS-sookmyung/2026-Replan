package com.yuan.replan.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
public class Todo {
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

    private String title;

    private LocalDateTime deadline;

    // Todo에 대한 사용자의 추가 설명
    private String additionalInfo;

    protected Todo() {
    }

    public Todo(String title, LocalDateTime deadline, String additionalInfo) {
        this.title = title;
        this.deadline = deadline;
        this.additionalInfo = additionalInfo;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
