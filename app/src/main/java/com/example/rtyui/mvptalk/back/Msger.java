package com.example.rtyui.mvptalk.back;

import com.google.gson.Gson;

public class Msger {

    public long time;
    public String label;
    public String msg;

    public Msger(long time, String label, String msg){
        this.time = time;
        this.label = label;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
