package com.example.rtyui.mvptalk.bean;

public class InfoActionBean {

    public int sendId;
    public String img;
    public String name;
    public int recvId;
    public long time;

    public InfoActionBean(){

    }

    public InfoActionBean(int sendId, String img, String name, int recvId, long time){
        this.sendId = sendId;
        this.img = img;
        this.name = name;
        this.recvId = recvId;
        this.time = time;
    }
}
