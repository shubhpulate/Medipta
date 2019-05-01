package com.app.mfitness.mediptaexpert;

import java.io.Serializable;

public class Group implements Serializable {
    String diet;
    String Calories;

    @Override
    public String toString() {
        return  diet + "\n" + Calories + "\n" +  "\n---------------\n";
    }
}
