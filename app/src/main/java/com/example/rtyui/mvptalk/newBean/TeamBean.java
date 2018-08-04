package com.example.rtyui.mvptalk.newBean;

import com.example.rtyui.mvptalk.model.TeamModel;

import java.io.Serializable;
import java.util.List;

public class TeamBean implements Serializable {

    public int id;
    public String nickname;
    public String headImgUrl;
    public int uid;
    public String remark = "备注";
}
