package com.yuan.replan.service;

import com.yuan.replan.entity.Plan;
import com.yuan.replan.entity.PlanItem;
import com.yuan.replan.repository.PlanItemRepository;
import com.yuan.replan.repository.PlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlanItemService {

    private final PlanItemRepository planItemRepository;
    private final PlanRepository planRepository;
    private final CurrentUser currentUser;

    public PlanItemService(PlanItemRepository planItemRepository,
                           PlanRepository planRepository, CurrentUser currentUser) {
        this.planItemRepository = planItemRepository;
        this.planRepository = planRepository;
        this.currentUser = currentUser;
    }

    public PlanItem savePlanItem(
            Plan plan,
            String content,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long durationMinutes) {

        requireOwnedPlan(plan.getId());
        PlanItem planItem = new PlanItem(
                plan,
                content,
                startTime,
                endTime,
                durationMinutes
        );
        return planItemRepository.save(planItem);
    }

    public List<PlanItem> getPlanItems(Long planId) {
        requireOwnedPlan(planId);
        return planItemRepository.findByPlanId(planId);
    }

    public void deletePlanItems(Long planId) {
        requireOwnedPlan(planId);
        planItemRepository.deleteByPlanId(planId);
    }

    private void requireOwnedPlan(Long planId) {
        planRepository.findByIdAndTodoUserId(planId, currentUser.id())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND));
    }
}
