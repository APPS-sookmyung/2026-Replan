package com.yuan.replan.service;

import com.yuan.replan.entity.Todo;
import com.yuan.replan.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo saveTodo(
            String title,
            LocalDateTime deadline,
            String additionalInfo) {

        Todo todo = new Todo(title, deadline, additionalInfo);

        return todoRepository.save(todo);
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Todo getTodoById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Todo를 찾을 수 없습니다."));
    }
}
