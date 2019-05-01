package com.app.mfitness.mediptaexpert;

import java.io.Serializable;

public class Tablet implements Serializable {
    public String tabletName;
    public String tabletTime;
    public String name;

    @Override
    public String toString() {
        return tabletName + "\n" + name + "\n" + tabletTime + "\n" + "\n----------------\n";
    }
}
