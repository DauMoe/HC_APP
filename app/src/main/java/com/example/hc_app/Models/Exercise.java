package com.example.hc_app.Models;

import java.io.Serializable;

public class Exercise implements Serializable {
    private int excerID;
    private float bmi_from, bmi_to;
    private String excer_name, description;

    public Exercise() {}

    public int getExcerID() {
        return excerID;
    }

    public void setExcerID(int excerID) {
        this.excerID = excerID;
    }

    public float getBmi_from() {
        return bmi_from;
    }

    public void setBmi_from(float bmi_from) {
        this.bmi_from = bmi_from;
    }

    public float getBmi_to() {
        return bmi_to;
    }

    public void setBmi_to(float bmi_to) {
        this.bmi_to = bmi_to;
    }

    public String getExcer_name() {
        return excer_name;
    }

    public void setExcer_name(String excer_name) {
        this.excer_name = excer_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
