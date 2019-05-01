package com.app.mfitness.mediptaexpert;

import java.io.Serializable;

public class Notification implements Serializable {
    public String message;
    public String count;
    public String activity;
    public String patientId;

    @Override
    public String toString() {
        return message + "\n" + count + "\n" + activity + "\n" + patientId + "\n" + "\n-------------\n";
    }
}
