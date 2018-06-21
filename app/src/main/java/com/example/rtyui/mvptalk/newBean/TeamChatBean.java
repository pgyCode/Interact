package com.example.rtyui.mvptalk.newBean;

import com.example.rtyui.mvptalk.tool.App;

public class TeamChatBean {

    public int sendId;
    public int recvId;
    public String msg;
    public long time;
    public int statu = App.MSG_SEND_ING;


    public TeamChatBean(int sendId, int recvId, String msg, long time){
        this.sendId = sendId;
        this.recvId = recvId;
        this.msg = msg;
        this.time = time;
    }


    public TeamChatBean(){

    }
}
