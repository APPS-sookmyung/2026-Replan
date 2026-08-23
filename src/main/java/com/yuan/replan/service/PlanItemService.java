package com.yuan.replan.service;

import com.yuan.replan.entity.Plan;
import com.yuan.replan.entity.PlanItem;
import com.yuan.replan.repository.PlanItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlanItemService {

    private final PlanItemRepository planItemRepository;

    public PlanItemService(PlanItemRepository planItemRepository) {
        this.planItemRepository = planItemRepository;
    }

    public PlanItem savePlanItem(
            Plan plan,
            String content,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long durationMinutes) {

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
        return planItemRepository.findByPlanId(planId);
    }
}
