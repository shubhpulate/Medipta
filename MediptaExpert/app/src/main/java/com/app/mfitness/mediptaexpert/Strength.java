package com.app.mfitness.mediptaexpert;

import java.io.Serializable;

public class Strength implements Serializable {
    public String exercise;
    public String type;
    public String image;
    public String description;

    @Override
    public String toString() {
        return exercise + "\n" + description + "\n" + type + "\n" + image + "\n" + "\n-------------\n";
    }
}
