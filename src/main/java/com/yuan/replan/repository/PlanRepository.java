package com.yuan.replan.repository;

import com.yuan.replan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findByTodoIdAndTodoUserId(Long todoId, Long userId);
    Optional<Plan> findByIdAndTodoUserId(Long id, Long userId);
    java.util.List<Plan> findAllByTodoUserId(Long userId);
}
