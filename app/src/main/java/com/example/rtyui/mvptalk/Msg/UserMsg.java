package com.example.rtyui.mvptalk.Msg;

import com.example.rtyui.mvptalk.bean.UserBean;

/**
 * Created by rtyui on 2018/4/25.
 */

public class UserMsg {
    private int code;
    private UserBean data;

    public void setData(UserBean data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public UserBean getData() {
        return data;
    }
}
