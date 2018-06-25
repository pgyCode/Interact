package com.example.rtyui.mvptalk.newBean;

import java.io.Serializable;

public class TeamMemberBean implements Serializable {

    public int id;
    public String nickname;
    public String headImgUrl;

    public TeamMemberBean(int id, String nickname, String headImgUrl){
        this.id = id;
        this.nickname = nickname;
        this.headImgUrl = headImgUrl;
    }
}
