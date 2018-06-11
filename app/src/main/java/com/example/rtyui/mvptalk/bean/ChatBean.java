package com.example.rtyui.mvptalk.bean;

/**
 * Created by rtyui on 2018/4/12.
 */

import com.example.rtyui.mvptalk.tool.App;

import java.io.Serializable;

/**
 * 存储一条chat消息对应的数据
 */
public class ChatBean{
    public int recvId;
    public int sendId;
    public int statu = App.MSG_SEND_ING;
    public long time;
    public String msg;
    public String sendNickname;
    public String sendHeadImgUrl;
    public String recvNickname;
    public String recvHeadImgUrl;

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



    public void setSendNickname(String sendNickname) {
        this.sendNickname = sendNickname;
    }

    public String getSendNickname() {
        return sendNickname;
    }

    public void setSendHeadImgUrl(String sendHeadImgUrl) {
        this.sendHeadImgUrl = sendHeadImgUrl;
    }

    public String getSendHeadImgUrl() {
        return sendHeadImgUrl;
    }

    public ChatBean(int recvId, int sendId, String msg, String sendNickname, String sendHeadImgUrl, String recvNickname, String recvHeadImgUrl, long time){
        this.recvId = recvId;
        this.sendId = sendId;
        this.msg = msg;
        this.sendNickname = sendNickname;
        this.sendHeadImgUrl = sendHeadImgUrl;
        this.recvNickname = recvNickname;
        this.recvHeadImgUrl = recvHeadImgUrl;
        this.time = time;
    }

    public void setrecvHeadImgUrl(String recvHeadImgUrl) {
        this.recvHeadImgUrl = recvHeadImgUrl;
    }

    public void setrecvNickname(String recvNickname) {
        this.recvNickname = recvNickname;
    }

    public String getrecvHeadImgUrl() {
        return recvHeadImgUrl;
    }

    public String getrecvNickname() {
        return recvNickname;
    }

    public ChatBean(){

    }
}
