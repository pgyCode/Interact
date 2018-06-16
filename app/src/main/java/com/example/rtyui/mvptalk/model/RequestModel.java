package com.example.rtyui.mvptalk.model;

import android.app.DownloadManager;

import com.example.rtyui.mvptalk.Msg.Msg;
import com.example.rtyui.mvptalk.Msg.MsgRequest;
import com.example.rtyui.mvptalk.bean.AddFriendBean;
import com.example.rtyui.mvptalk.bean.UserBean;
import com.example.rtyui.mvptalk.parent.Model;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MyLocalObject;
import com.example.rtyui.mvptalk.tool.NetVisitor;
import com.google.gson.Gson;

import java.util.List;

public class RequestModel extends Model {

    private static RequestModel instance;

    public static RequestModel getInstance() {
        if (instance == null)
            instance = new RequestModel();
        return instance;
    }
    public static synchronized void dstroyInstance() {
        if (instance != null) instance = null;
    }



    public List<AddFriendBean> addFriendBeans = null;


    public RequestModel(){

    }


    /**
     * 发送好友请求
     * @param userId
     * @return 是否成功
     */
    public int addFriend(int userId) {
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/friend/request", "sendId=" +
                    AccountModel.getInstance().currentUser.id +
                    "&recvId=" +
                    userId);
            Msg msg = new Gson().fromJson(temp, Msg.class);
            return msg.code;
        }
        catch(Exception e){
            return -1;
        }
    }

    /**
     * 加载全部好友请求
     * @return 请求结果
     */
    public int loadRequest(){
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/friend/getAllReq", "userId=" + AccountModel.getInstance().currentUser.id);
            System.out.println(App.host + "Talk/friend/getAllReq" + "?" + "userId=" + AccountModel.getInstance().currentUser.id);
            System.out.println(temp);
            MsgRequest msg = new Gson().fromJson(temp, MsgRequest.class);
            addFriendBeans = msg.data;
            return msg.code;
        }
        catch(Exception e){}
        return -1;
    }


    /**
     * 同意请求
     * @param position
     * @return 是否发送成功
     */
    public int AgreeRequest(int position){
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/friend/agree", "sendId=" +
                    addFriendBeans.get(position).sendId +
                    "&recvId=" +
                    addFriendBeans.get(position).recvId);
            Msg msg = new Gson().fromJson(temp, Msg.class);
            FriendModel.getInstance().linkFriends.add(new UserBean(addFriendBeans.get(position).sendId, addFriendBeans.get(position).sendNickname, addFriendBeans.get(position).sendHeadImgUrl));
            //addFriendBeans.remove(position);
            MyLocalObject.saveObject("linkFriends", FriendModel.getInstance().linkFriends);
            return msg.code;
        }
        catch(Exception e){}
        return -1;
    }

    /**
     * 拒绝好友请求
     * @param position
     * @return
     */
    public int DisAgreeRequest(int position){
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/friend/disagree", "sendId=" +
                    addFriendBeans.get(position).sendId +
                    "&recvId=" +
                    addFriendBeans.get(position).recvId);
            Msg msg = new Gson().fromJson(temp, Msg.class);
            addFriendBeans.remove(position);
            return msg.code;
        }
        catch(Exception e){}
        return -1;
    }
}
