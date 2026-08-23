package com.yuan.replan.dto;

import com.yuan.replan.entity.PlanItem;

import java.util.List;

public class PlanResponse {

    private String reason;
    private List<PlanItem> items;

    public PlanResponse(String reason, List<PlanItem> items) {
        this.reason = reason;
        this.items = items;
    }

    public String getReason() {
        return reason;
    }

    public List<PlanItem> getItems() {
        return items;
    }
}
