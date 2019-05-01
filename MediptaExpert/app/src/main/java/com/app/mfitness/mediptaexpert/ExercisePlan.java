package com.app.mfitness.mediptaexpert;

import java.io.Serializable;

public class ExercisePlan implements Serializable {
    public String exercisePlan;
    public String name;
    public String startDate;
    public String endDate;
    public String isActive;

    @Override
    public String toString() {
        return exercisePlan + "\n" + isActive + "\n" + startDate + "\n" + endDate + "\n" + name + "\n" + "\n---------------\n";
    }
}
