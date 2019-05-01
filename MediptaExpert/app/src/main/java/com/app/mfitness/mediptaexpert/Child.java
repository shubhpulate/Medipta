package com.app.mfitness.mediptaexpert;

import java.io.Serializable;

public class Child implements Serializable {
    public String food;
    public String quantity;
    public String name;
    public String itemType;

    @Override
    public String toString() {
        return food + "\n" + itemType + "\n" + quantity + "\n" + "\n----------------\n";
    }
}
