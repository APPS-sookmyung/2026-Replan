package com.yuan.replan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "todo_id", nullable = false, unique = true)
    private Todo todo;

    private String reason;

    private LocalDateTime createdAt;

    protected Plan() {
    }

    public Plan(Todo todo, String reason) {
        this.todo = todo;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Todo getTodo() {
        return todo;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
