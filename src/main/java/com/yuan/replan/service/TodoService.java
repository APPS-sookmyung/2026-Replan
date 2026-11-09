package com.yuan.replan.service;

import com.yuan.replan.entity.Todo;
import com.yuan.replan.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TodoService {
    private final CurrentUser currentUser;

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository, CurrentUser currentUser) {
        this.currentUser = currentUser;
        this.todoRepository = todoRepository;
    }

    public Todo saveTodo(
            String title,
            LocalDateTime deadline,
            String additionalInfo) {

        Todo todo = new Todo(title, deadline, additionalInfo);

        todo.setUser(currentUser.get());
        return todoRepository.save(todo);
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAllByUserId(currentUser.id());
    }

    public Todo getTodoById(Long id) {
        return todoRepository.findByIdAndUserId(id, currentUser.id())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
