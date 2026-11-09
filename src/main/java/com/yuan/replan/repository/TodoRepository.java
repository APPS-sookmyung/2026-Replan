package com.yuan.replan.repository;

import com.yuan.replan.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    java.util.List<Todo> findAllByUserId(Long userId);
    java.util.Optional<Todo> findByIdAndUserId(Long id, Long userId);
}
