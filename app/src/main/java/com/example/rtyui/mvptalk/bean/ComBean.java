package com.example.rtyui.mvptalk.bean;

import com.example.rtyui.mvptalk.tool.App;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rtyui on 2018/5/2.
 */

public class ComBean implements Serializable {
    public int id;
    public int category = App.CATEGORY_FRIEND;
    public List<ChatBean> chats;

    public ComBean(int id, int category, List<ChatBean> chats){
        this.category = category;
        this.id = id;
        this.chats = chats;
    }
}
