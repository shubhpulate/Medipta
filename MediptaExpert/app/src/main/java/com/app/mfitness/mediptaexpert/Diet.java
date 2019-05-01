package com.app.mfitness.mediptaexpert;

import java.io.Serializable;

public class Diet implements Serializable{
    public String itemName;
    public String itemType;
    public String nameId;
    public String calories;
    public String unit;

    @Override
    public String toString() {
        return itemName + "\n" + calories + "\n" + unit + "\n" + nameId + "\n" + itemType + "\n"  + "\n---------------\n";
    }
}
