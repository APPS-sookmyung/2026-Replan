package com.yuan.replan.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private LocalDateTime deadline;
    protected Todo(){
    }
    public Todo(String title, LocalDateTime deadline){
        this.title = title;
        this.deadline = deadline;
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
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

}
