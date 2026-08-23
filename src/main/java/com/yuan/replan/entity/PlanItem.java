package com.yuan.replan.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    private String content;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long durationMinutes;

    protected PlanItem() {
    }

    public PlanItem(
            Plan plan,
            String content,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long durationMinutes) {

        this.plan = plan;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
    }

    public Long getId() {
        return id;
    }

    public Plan getPlan() {
        return plan;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }
}
