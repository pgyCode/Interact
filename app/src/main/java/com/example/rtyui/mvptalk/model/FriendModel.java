package com.example.rtyui.mvptalk.model;

import android.accounts.Account;
import android.icu.text.UFormat;

import com.example.rtyui.mvptalk.Msg.GroupMsg;
import com.example.rtyui.mvptalk.Msg.Msg;
import com.example.rtyui.mvptalk.Msg.MsgLinkFriend;
import com.example.rtyui.mvptalk.Msg.MsgRequest;
import com.example.rtyui.mvptalk.Msg.UserMsg;
import com.example.rtyui.mvptalk.bean.AddFriendBean;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.bean.Group;
import com.example.rtyui.mvptalk.bean.GroupBean;
import com.example.rtyui.mvptalk.bean.UserBean;
import com.example.rtyui.mvptalk.parent.Model;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MyLocalObject;
import com.example.rtyui.mvptalk.tool.MySqliteHelper;
import com.example.rtyui.mvptalk.tool.NetVisitor;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.Source;

/**
 * Created by rtyui on 2018/4/25.
 */

public class FriendModel extends Model {


    private static FriendModel instance;

    public static FriendModel getInstance() {
        if (instance == null)
            instance = new FriendModel();
        return instance;
    }

    public FriendModel(){
        if (AccountModel.getInstance().currentUser != null) {
            linkFriends = (List<UserBean>) MyLocalObject.getObject("linkFriends");
        }
    }

    public List<UserBean> linkFriends = null;


    /**
     * 辅助函数
     * @param id
     * @return 对应id的用户
     */
    public UserBean getUserById(int id) {
        for (UserBean userBean : linkFriends) {
            if (userBean.id == id)
                return userBean;
        }
        return null;
    }

    /**
     * 请求好友列表
     * @return 请求结果
     */
    public int flush() {
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/friend/getAllFri?", "userId=" +
                    AccountModel.getInstance().currentUser.id);
            MsgLinkFriend msg = new Gson().fromJson(temp, MsgLinkFriend.class);
            linkFriends = msg.data;
            MyLocalObject.saveObject("linkFriends", linkFriends);
            return msg.code;
        }
        catch(Exception e){
            return App.NET_FAil;
        }
    }

    /**
     * 修改备注
     * @param remark 备注
     * @param id
     * @return 请求结果
     */
    public int changeRemark(String remark, int id){
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/friend/updateRemark", "userId=" +
                    AccountModel.getInstance().currentUser.id +
                    "&guestId=" +
                    id +
                    "&remark=" +
                    remark);
            Msg msg = new Gson().fromJson(temp, Msg.class);
            if (msg.code == App.NET_SUCCEED)
            {
                FriendModel.getInstance().getUserById(id).nickname = remark;
                MyLocalObject.saveObject("linkFriends", linkFriends);
            }
            return msg.code;
        }
        catch(Exception e){
            return App.NET_FAil;
        }
    }

    /**
     * 删除好友
     * @param id
     * @return 请求结果
     */
    public int delFriend(int id){
        try {
            String temp = NetVisitor.postNormal(App.host + "Talk/friend/delFriend?", "userId=" +
                    AccountModel.getInstance().currentUser.id +
                    "&guestId" +
                    "=" +
                    id
            );
            Msg msg = new Gson().fromJson(temp, Msg.class);
            if (msg.code == App.NET_SUCCEED)
            {
                linkFriends.remove(getUserById(id));
                MsgModel.getInstance().comBeans.remove(MsgModel.getInstance().getCombeanById(id));
                MySqliteHelper.getInstance().delete(ChatBean.class, "recvId=" +
                        id +
                        " or sendId=" +
                        id);
                MyLocalObject.saveObject("linkFriends", linkFriends);
            }
            return msg.code;
        }catch (Exception e){
            return App.NET_FAil;
        }
    }

    /**
     * 请求要添加的人信息
     * @param id
     * @return 请求返回内容
     */
    public String getFriend(int id){
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/friend/getInformation", "searchId=" +
                    id);
            return temp;
        }
        catch(Exception e){
            return null;
        }
    }
}
