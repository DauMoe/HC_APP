package com.example.hc_app.Models;

public class ChartData {
    private int total_step;
    private String starttime, endtime, endtimestamp;

    public ChartData() {}

    public int getTotal_step() {
        return total_step;
    }

    public void setTotal_step(int total_step) {
        this.total_step = total_step;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getEndtimestamp() {
        return endtimestamp;
    }

    public void setEndtimestamp(String endtimestamp) {
        this.endtimestamp = endtimestamp;
    }
}
