package com.yuan.replan.controller;

import com.yuan.replan.dto.PlanResponse;
import com.yuan.replan.entity.Todo;
import com.yuan.replan.service.GeminiService;
import com.yuan.replan.service.KeywordService;
import com.yuan.replan.service.PlanService;
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
    private final PlanService planService;

    public TodoController(
            TodoService todoService,
            GeminiService geminiService,
            KeywordService keywordService,
            PlanService planService) {

        this.todoService = todoService;
        this.geminiService = geminiService;
        this.keywordService = keywordService;
        this.planService = planService;
    }

    @PostMapping
    public Todo saveTodo(
            @RequestParam String title,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam LocalDateTime deadline,
            @RequestParam(required = false) String additionalInfo) {

        return todoService.saveTodo(title, deadline, additionalInfo);
    }

    @GetMapping
    public List<Todo> getAllTodos() {
        return todoService.getAllTodos();
    }

    @PostMapping("/{todoId}/plan")
    public String generatePlan(@PathVariable Long todoId) {

        Todo todo = todoService.getTodoById(todoId);

        String keywords = keywordService.getWeightedKeywordsAsString();

        String planJson = geminiService.generatePlan(
                todo.getTitle(),
                todo.getDeadline(),
                todo.getAdditionalInfo(),
                keywords
        );

        planService.saveGeneratedPlan(todo, planJson);

        return planJson;
    }

    @GetMapping("/{todoId}/plan")
    public PlanResponse getPlan(@PathVariable Long todoId) {

        return planService.getPlanResponse(todoId);
    }
}
