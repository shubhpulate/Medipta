package com.app.mfitness.mediptaexpert;

import java.io.Serializable;

public class Article implements Serializable {
    public String title;
    public String description;
    public String image;
    public String author;
    public String name;
    public String drID;

    @Override
    public String toString() {
        return title + "\n" + drID + "\n" + name + "\n" + description + "\n" + image + "\n" + author + "\n" + "\n--------------\n";
    }
}
