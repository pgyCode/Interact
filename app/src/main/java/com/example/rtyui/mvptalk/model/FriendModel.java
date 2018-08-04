package com.example.rtyui.mvptalk.model;

import com.example.rtyui.mvptalk.Msg.Msg;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.bean.ComBean;
import com.example.rtyui.mvptalk.newBean.FriendBean;
import com.example.rtyui.mvptalk.newMsg.FriendFlushMsg;
import com.example.rtyui.mvptalk.parent.Model;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MyLocalObject;
import com.example.rtyui.mvptalk.tool.NetVisitor;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

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

    public static synchronized void dstroyInstance() {
        if (instance != null) instance = null;
    }

    public void init(){
        if (AccountModel.getInstance().currentUser != null) {
            linkFriends = (List<FriendBean>) MyLocalObject.getObject("linkFriends_" + AccountModel.getInstance().currentUser.id);
        }
    }

    public List<FriendBean> linkFriends = null;
    public int CURRENT_TALK = -1;


    /**
     * 辅助函数
     * @param id
     * @return 对应id的用户
     */
    public FriendBean getUserById(int id) {
        for (FriendBean friendBean : linkFriends) {
            if (friendBean.id == id)
                return friendBean;
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
            FriendFlushMsg msg = new Gson().fromJson(temp, FriendFlushMsg.class);
            if (msg.code == App.NET_SUCCEED){
                linkFriends = msg.data;
                MyLocalObject.saveObject("linkFriends_" + AccountModel.getInstance().currentUser.id, linkFriends);
                if (MsgModel.getInstance().comBeans != null) {
                    for (ComBean bean : MsgModel.getInstance().comBeans) {
                        if ((bean.category == App.CATEGORY_FRIEND && FriendModel.getInstance().getUserById(bean.id) == null) ||
                                (bean.category == App.CATEGORY_TEAM && TeamModel.getInstance().OUTER_getTeamById(bean.id) == null) ) {
                            MsgModel.getInstance().delFriendFromLocal(bean.id);
                        }
                    }
                }
            }
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
                FriendModel.getInstance().getUserById(id).remark = remark;
                MyLocalObject.saveObject("linkFriends_" + AccountModel.getInstance().currentUser.id, linkFriends);
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
                delFriendFromLocal(id);
            }
            return msg.code;
        }catch (Exception e){
            return App.NET_FAil;
        }
    }

    public void delFriendFromLocal(int id){
        linkFriends.remove(getUserById(id));
        MyLocalObject.saveObject("linkFriends_" + AccountModel.getInstance().currentUser.id, linkFriends);
        MsgModel.getInstance().delFriendFromLocal(id);
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


    /**
     * 添加一个好友
     */
    public void addOne(FriendBean friendBean){
        if (linkFriends == null)
            linkFriends = new ArrayList<>();
        linkFriends.add(friendBean);
    }
}
