package com.example.rtyui.mvptalk.newBean;

import java.util.List;

public class TeamComBean {

    public int id;
    public int unread;
    public List<TeamChatBean> chats;

    public TeamComBean(int id, List<TeamChatBean> chats){
        this.id = id;
        this.chats = chats;
    }
}
