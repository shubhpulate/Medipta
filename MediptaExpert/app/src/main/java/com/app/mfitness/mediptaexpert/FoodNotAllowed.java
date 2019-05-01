package com.app.mfitness.mediptaexpert;

import java.io.Serializable;

public class FoodNotAllowed implements Serializable {
    public String foodNotAllowed;
    public String name;

    @Override
    public String toString() {
        return foodNotAllowed + "\n" + name + "\n" + "\n-------------------\n";
    }
}
