package com.example.rtyui.mvptalk.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rtyui on 2018/5/2.
 */

public class ComBean implements Serializable {
    public int userId;
    public String nickname;
    public String headImgUrl;
    public int unread;
    public long time;
    public List<ChatBean> chats;

    public ComBean(int userId, String nickname, String headImgUrl, List<ChatBean> chats){
        this.userId = userId;
        this.nickname = nickname;
        this.headImgUrl = headImgUrl;
        this.chats = chats;
    }
}
