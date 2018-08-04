package com.example.rtyui.mvptalk.newBean;

import java.io.Serializable;

public class FriendBean implements Serializable {

    public int id;
    public String nickname;
    public String headImgUrl;
    public String remark;

    public FriendBean(int id, String nickname, String headImgUrl, String remark){
        this.id = id;
        this.nickname = nickname;
        this.headImgUrl = headImgUrl;
        this.remark = remark;
    }

    public FriendBean(){}
}
