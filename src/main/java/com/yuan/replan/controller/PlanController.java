package com.yuan.replan.controller;

import com.yuan.replan.entity.Plan;
import com.yuan.replan.service.PlanService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping("/plans")
    public List<Plan> getAllPlans() {
        return planService.getAllPlans();
    }

    @DeleteMapping("/plans/{planId}")
    public void deletePlan(@PathVariable Long planId) {
        planService.deletePlan(planId);
    }
}
