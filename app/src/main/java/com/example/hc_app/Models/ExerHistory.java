package com.example.hc_app.Models;

public class ExerHistory {
    private int historyID, userID, gr_excerID, excerID, step, stepofday;
    private float distanceofday, distance, calo;
    private Long starttime, endtime;

    public ExerHistory() {
    }

    public ExerHistory(int historyID, int userID, int gr_excerID, int excerID, int step, int stepofday, float distanceofday, float distance, float calo, Long starttime, Long endtime) {
        this.historyID = historyID;
        this.userID = userID;
        this.gr_excerID = gr_excerID;
        this.excerID = excerID;
        this.step = step;
        this.stepofday = stepofday;
        this.distanceofday = distanceofday;
        this.distance = distance;
        this.calo = calo;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    public int getHistoryID() {
        return historyID;
    }

    public void setHistoryID(int historyID) {
        this.historyID = historyID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getGr_excerID() {
        return gr_excerID;
    }

    public void setGr_excerID(int gr_excerID) {
        this.gr_excerID = gr_excerID;
    }

    public int getExcerID() {
        return excerID;
    }

    public void setExcerID(int excerID) {
        this.excerID = excerID;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getStepofday() {
        return stepofday;
    }

    public void setStepofday(int stepofday) {
        this.stepofday = stepofday;
    }

    public float getDistanceofday() {
        return distanceofday;
    }

    public void setDistanceofday(float distanceofday) {
        this.distanceofday = distanceofday;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getCalo() {
        return calo;
    }

    public void setCalo(float calo) {
        this.calo = calo;
    }

    public Long getStarttime() {
        return starttime;
    }

    public void setStarttime(Long starttime) {
        this.starttime = starttime;
    }

    public Long getEndtime() {
        return endtime;
    }

    public void setEndtime(Long endtime) {
        this.endtime = endtime;
    }
}
