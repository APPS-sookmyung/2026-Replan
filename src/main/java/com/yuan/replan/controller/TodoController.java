package com.yuan.replan.controller;

import com.yuan.replan.entity.Todo;
import com.yuan.replan.service.GeminiService;
import com.yuan.replan.service.KeywordService;
import com.yuan.replan.service.TodoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/todo")
public class TodoController {
    private final TodoService todoService;
    private final GeminiService geminiService;
    private final KeywordService keywordService;

    public TodoController(
            TodoService todoService,
            GeminiService geminiService,
            KeywordService keywordService) {
        this.todoService = todoService;
        this.geminiService = geminiService;
        this.keywordService = keywordService;
    }

    @PostMapping
    public Todo saveTodo(
            @RequestParam String title,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam LocalDateTime deadline) {

        return todoService.saveTodo(title, deadline);
    }

    @GetMapping
    public List<Todo> getAllTodos() {
        return todoService.getAllTodos();
    }

    @PostMapping("/plan")
    public String generatePlan(
            @RequestParam String title,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam LocalDateTime deadline) {
        String keywords = keywordService.getKeywordsAsString();

        return geminiService.generatePlan(title, deadline, keywords);
    }
}
