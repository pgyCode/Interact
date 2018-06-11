package com.example.rtyui.mvptalk.bean;

import java.io.Serializable;

import javax.crypto.interfaces.PBEKey;

/**
 * Created by rtyui on 2018/4/25.
 */

public class UserBean implements Serializable{
    public int id;
    public String nickname;
    public String headImgUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public UserBean(int id, String nickname, String headImgUrl){
        this.id = id;
        this.nickname = nickname;
        this.headImgUrl = headImgUrl;
    }
}
