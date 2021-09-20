package com.example.hc_app.Models;

import java.util.ArrayList;

public class RespObj {
    private int code;
    private ArrayList<?> msg;

    public RespObj(int code, ArrayList<?> msg) {
        this.code = code;
        this.msg = msg;
    }

    public RespObj() {}

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<?> getMsg() {
        return msg;
    }

    public void setMsg(ArrayList<?> msg) {
        this.msg = msg;
    }
}
