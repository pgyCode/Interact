package com.example.rtyui.mvptalk.Msg;

import com.example.rtyui.mvptalk.bean.ChatBean;

/**
 * 通信时候的消息
 */
public class MsgChat {

    //特征值
    private int code;

    //数据
    private ChatBean data;

    public void setData(ChatBean data) {
        this.data = data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public ChatBean getData() {
        return data;
    }

    public MsgChat(int code, ChatBean data){
        this.code = code;
        this.data = data;
    }
}