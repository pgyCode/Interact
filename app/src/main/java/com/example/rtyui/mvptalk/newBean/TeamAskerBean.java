package com.example.rtyui.mvptalk.newBean;

public class TeamAskerBean {
    public int uid;
    public int tid;
    public String tname;
    public String headImgUrl;
    public String userName;
    public String userHeadImgUrl;
    public long time;

    public TeamAskerBean(int uid, int tid){

    }

    public TeamAskerBean(){

    }

    public TeamAskerBean(int uid, int tid, String img, String name, long time){
        this.uid = uid;
        this.tid = tid;
        this.userHeadImgUrl = img;
        this.userName = name;
        this.time = time;
    }
}
