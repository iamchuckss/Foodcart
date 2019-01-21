package com.iamchuckss.foodcart.models;

import java.util.ArrayList;

public class DayPlan {
    private ArrayList<Recipe> daily_plan;

    public DayPlan(ArrayList<Recipe> daily_plan) {
        this.daily_plan = daily_plan;
    }

    public DayPlan() {
    }

    public ArrayList<Recipe> getDaily_plan() {
        return daily_plan;
    }

    public void setDaily_plan(ArrayList<Recipe> daily_plan) {
        this.daily_plan = daily_plan;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "daily_plan=" + daily_plan +
                '}';
    }
}
