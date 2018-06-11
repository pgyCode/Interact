package com.example.rtyui.mvptalk.bean;

/**
 * Created by rtyui on 2018/5/12.
 */

public class AddFriendBean {

    public int sendId;
    public String sendNickname;
    public String sendHeadImgUrl;

    public int recvId;


    public AddFriendBean(int sendId, String sendNickname, String sendHeadImgUrl, int recvId){
        this.sendId = sendId;
        this.sendNickname = sendNickname;
        this.sendHeadImgUrl = sendHeadImgUrl;
        this.recvId = recvId;
    }
}
