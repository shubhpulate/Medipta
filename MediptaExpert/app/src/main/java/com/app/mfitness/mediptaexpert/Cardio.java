package com.app.mfitness.mediptaexpert;

import java.io.Serializable;

public class Cardio implements Serializable {
    public String cardioName;
    public String cardioType;
    public String cardioImage;
    public String description;

    @Override
    public String toString() {
        return cardioName + "\n" + description + "\n" + cardioType + "\n" + cardioImage + "\n" + "\n---------\n";
    }
}
