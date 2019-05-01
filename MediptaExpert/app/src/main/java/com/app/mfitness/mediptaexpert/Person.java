package com.app.mfitness.mediptaexpert;

import java.io.Serializable;

public class Person implements Serializable {
    public String name;
    public String contact;
    public String fitnessGoal;
    public int age;
    public String BMI;
    public String gender;
    public String email;
    public String goalId;
    public String profileId;

    @Override
    public String toString() {
        return name + "\n"+ profileId + "\n" +  goalId + "\n" + age + "\n" + BMI + "\n" + gender + "\n" +  email + "\n" + fitnessGoal + "\n" + contact + "\n" + "\n---------------\n";
    }
}
