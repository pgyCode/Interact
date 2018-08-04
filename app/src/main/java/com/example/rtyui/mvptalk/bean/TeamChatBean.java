package com.example.rtyui.mvptalk.bean;

/**
 * Created by rtyui on 2018/4/12.
 */

import com.example.rtyui.mvptalk.tool.App;

import java.io.Serializable;

/**
 * 存储一条chat消息对应的数据
 */
public class TeamChatBean implements Serializable{
    public int recvId;
    public int sendId;
    public int category = App.CATEGORY_FRIEND;
    public int statu = App.MSG_SEND_ING;
    public int read = App.MSG_READ;
    public long time;
    public String msg;
    public String remark;
    public String name;
    public String head;

    public TeamChatBean(int recvId, int sendId, String msg, long time, int category, String remark, String name, String head){
        this.recvId = recvId;
        this.sendId = sendId;
        this.msg = msg;
        this.time = time;
        this.category = category;
        this.remark = remark;
        this.name = name;
        this.head = head;
    }

    public TeamChatBean(){

    }
}
