package com.app.mfitness.mediptaexpert;

import java.io.Serializable;

public class DietPlan implements Serializable {
    public String dietPlan;
    public String name;
    public String startDate;
    public String endDate;
    public String userId;
    public String isActive;

    @Override
    public String toString() {
        return dietPlan + "\n" + isActive + "\n" + userId + "\n" + name + "\n" + startDate + "\n" + endDate + "\n" + "\n---------------\n";

    }
}
