package com.yuan.replan.repository;

import com.yuan.replan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findByTodoId(Long todoId);
}
