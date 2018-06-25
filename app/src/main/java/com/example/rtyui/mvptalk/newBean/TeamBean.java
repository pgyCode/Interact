package com.example.rtyui.mvptalk.newBean;

import com.example.rtyui.mvptalk.model.TeamModel;

import java.io.Serializable;
import java.util.List;

public class TeamBean implements Serializable {

    public int id;
    public String nickname;
    public String headImgUrl;
    public List<TeamMemberBean> members;

    public TeamBean(int id, String nickname, String headImgUrl){
        this.id = id;
        this.nickname = nickname;
        this.headImgUrl = headImgUrl;
    }
}
