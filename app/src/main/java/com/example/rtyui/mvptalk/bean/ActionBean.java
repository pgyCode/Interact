package com.example.rtyui.mvptalk.bean;

import com.example.rtyui.mvptalk.newBean.FriendBean;

public class ActionBean {

    public int sendId;
    public int recvId;
    public long time;

    public ActionBean(){

    }

    public ActionBean(int sendId, int recvId, long time){
        this.sendId = sendId;
        this.recvId = recvId;
        this.time = time;
    }
}
