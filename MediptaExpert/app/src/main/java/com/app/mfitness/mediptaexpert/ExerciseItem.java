package com.app.mfitness.mediptaexpert;

public class ExerciseItem {
    public String exercise;
    public String imageUrl;
    public String type;
    public String description;
    public String name;

    @Override
    public String toString() {
        return exercise + "\n"+ name + "\n" + description + "\n" + imageUrl + "\n" + type + "\n" + "\n----------\n";
    }
}
