package com.example.hc_app.Models;

public class Historys {
    private int userID, excerID, gr_excerID;
    private Long starttime, endtime, datestamp;
    private String excer_name, gr_name;

    public Historys() {}

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getExcerID() {
        return excerID;
    }

    public void setExcerID(int excerID) {
        this.excerID = excerID;
    }

    public int getGr_excerID() {
        return gr_excerID;
    }

    public void setGr_excerID(int gr_excerID) {
        this.gr_excerID = gr_excerID;
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

    public Long getDatestamp() {
        return datestamp;
    }

    public void setDatestamp(Long datestamp) {
        this.datestamp = datestamp;
    }

    public String getExcer_name() {
        return excer_name;
    }

    public void setExcer_name(String excer_name) {
        this.excer_name = excer_name;
    }

    public String getGr_name() {
        return gr_name;
    }

    public void setGr_name(String gr_name) {
        this.gr_name = gr_name;
    }
}
