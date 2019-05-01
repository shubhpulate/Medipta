package com.app.mfitness.mediptaexpert;

import java.io.Serializable;

public class UpcomingCall implements Serializable {
    public String name;
    public String contact;
    public String time;

    @Override
    public String toString() {
        return  name + "\n" + contact + "\n" + time + "\n" +  "\n---------------\n";
    }
}
