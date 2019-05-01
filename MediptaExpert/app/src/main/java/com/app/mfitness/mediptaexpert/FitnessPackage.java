package com.app.mfitness.mediptaexpert;

import java.io.Serializable;

public class FitnessPackage implements Serializable {
    public String packageName;
    public String packageType;
    public int exercisePlans;
    public String duration;
    public String dietPlans;
    public String calls;
    public String price;
    public String imageUrl;

    @Override
    public String toString() {
        return packageName + "\n" + imageUrl + "\n" + packageType + "\n"+ exercisePlans + "\n"   + duration + "\n" + dietPlans + "\n" + calls + "\n" + price + "\n" + "\n---------------\n";
    }
}
