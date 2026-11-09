package com.yuan.replan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuan.replan.dto.PlanResponse;
import com.yuan.replan.entity.Plan;
import com.yuan.replan.entity.PlanItem;
import com.yuan.replan.entity.Todo;
import com.yuan.replan.repository.PlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlanService {
    private final CurrentUser currentUser;

    private final PlanRepository planRepository;
    private final PlanItemService planItemService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PlanService(
            PlanRepository planRepository,
            PlanItemService planItemService, CurrentUser currentUser) {

        this.currentUser = currentUser;
        this.planRepository = planRepository;
        this.planItemService = planItemService;
    }

    @Transactional
    public Plan saveGeneratedPlan(Todo todo, String json) {
        if (todo.getUser() == null || !currentUser.id().equals(todo.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        planRepository.findByTodoIdAndTodoUserId(todo.getId(), currentUser.id()).ifPresent(existing -> {
            planItemService.deletePlanItems(existing.getId());
            planRepository.delete(existing);
            planRepository.flush();
        });

        try {
            JsonNode root = objectMapper.readTree(json);

            String reason = root.get("reason").asText();

            Plan plan = new Plan(todo, reason);
            Plan savedPlan = planRepository.save(plan);

            JsonNode items = root.get("items");

            for (JsonNode item : items) {

                String content = item.get("content").asText();

                LocalDateTime startTime =
                        LocalDateTime.parse(item.get("startTime").asText());

                LocalDateTime endTime =
                        LocalDateTime.parse(item.get("endTime").asText());

                Long durationMinutes =
                        item.get("durationMinutes").asLong();

                planItemService.savePlanItem(
                        savedPlan,
                        content,
                        startTime,
                        endTime,
                        durationMinutes
                );
            }

            return savedPlan;

        } catch (Exception e) {
            throw new RuntimeException("AI 계획 저장에 실패했습니다.", e);
        }

    }

    public Plan getPlanByTodoId(Long todoId) {
        return planRepository.findByTodoIdAndTodoUserId(todoId, currentUser.id())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public PlanResponse getPlanResponse(Long todoId) {

        Plan plan = getPlanByTodoId(todoId);

        List<PlanItem> items =
                planItemService.getPlanItems(plan.getId());

        return new PlanResponse(
                plan.getReason(),
                items
        );
    }

    public List<Plan> getAllPlans() {
        return planRepository.findAllByTodoUserId(currentUser.id());
    }

    @Transactional
    public void deletePlan(Long planId) {
        Plan plan = planRepository.findByIdAndTodoUserId(planId, currentUser.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        planItemService.deletePlanItems(planId);
        planRepository.delete(plan);
    }
}
