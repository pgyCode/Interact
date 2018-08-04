package com.example.rtyui.mvptalk.bean;

/**
 * Created by rtyui on 2018/4/12.
 */

import com.example.rtyui.mvptalk.tool.App;

import java.io.Serializable;

/**
 * 存储一条chat消息对应的数据
 */
public class ChatBean implements Serializable{
    public int recvId;
    public int sendId;
    public int category = App.CATEGORY_FRIEND;
    public int statu = App.MSG_SEND_ING;
    public int read = App.MSG_READ;
    public long time;
    public String msg;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public int getSendId() {
        return sendId;
    }

    public void setSendId(int sendId) {
        this.sendId = sendId;
    }

    public void setrecvId(int recvId) {
        this.recvId = recvId;
    }

    public int getrecvId() {
        return recvId;
    }

    public ChatBean(int recvId, int sendId, String msg, long time, int category){
        this.recvId = recvId;
        this.sendId = sendId;
        this.msg = msg;
        this.time = time;
        this.category = category;
    }

    public ChatBean(){

    }
}
