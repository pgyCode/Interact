package com.example.rtyui.mvptalk.model;

import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.bean.TeamChatBean;
import com.example.rtyui.mvptalk.newBean.FriendBean;
import com.example.rtyui.mvptalk.tool.MyLocalObject;

import java.util.ArrayList;
import java.util.List;

public class TempUserModel {

    private static TempUserModel instance;

    public static TempUserModel getInstance() {
        if (instance == null)
            instance = new TempUserModel();
        return instance;
    }

    public static synchronized void dstroyInstance() {
        if (instance != null) instance = null;
    }

    public void init(){
        if (AccountModel.getInstance().currentUser != null) {
            tempFriends = (List<FriendBean>) MyLocalObject.getObject("tempFriends_" + AccountModel.getInstance().currentUser.id);
        }
    }

    public List<FriendBean> tempFriends = null;


    public void add(TeamChatBean chatBean){
        if (tempFriends == null)
            tempFriends = new ArrayList<>();
        FriendBean friendBean = null;
        for (FriendBean bean : tempFriends){
            if (bean.id == chatBean.sendId){
                friendBean = bean;
            }
        }
        if (friendBean == null)
            friendBean = new FriendBean();
        friendBean.id = chatBean.sendId;
        friendBean.remark = chatBean.remark;
        friendBean.headImgUrl = chatBean.head;
        friendBean.nickname = chatBean.name;
        tempFriends.add(friendBean);
        MyLocalObject.saveObject("tempFriends_" + AccountModel.getInstance().currentUser.id, tempFriends);
    }


    public FriendBean getBeanById(int id){
        if (tempFriends != null){
            for (FriendBean bean : tempFriends){
                if (bean.id == id)
                    return bean;
            }
        }
        return null;
    }
}
