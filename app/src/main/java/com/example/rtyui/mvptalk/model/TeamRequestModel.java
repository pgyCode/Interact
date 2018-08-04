package com.example.rtyui.mvptalk.model;

import com.example.rtyui.mvptalk.Msg.Msg;
import com.example.rtyui.mvptalk.Msg.MsgRequest;
import com.example.rtyui.mvptalk.bean.AddFriendBean;
import com.example.rtyui.mvptalk.newBean.FriendBean;
import com.example.rtyui.mvptalk.newBean.TeamAskerBean;
import com.example.rtyui.mvptalk.newMsg.MsgTeamAsker;
import com.example.rtyui.mvptalk.parent.Model;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MyLocalObject;
import com.example.rtyui.mvptalk.tool.NetVisitor;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TeamRequestModel extends Model {

    private static TeamRequestModel instance;

    public static TeamRequestModel getInstance() {
        if (instance == null)
            instance = new TeamRequestModel();
        return instance;
    }
    public static synchronized void dstroyInstance() {
        if (instance != null) instance = null;
    }



    public List<TeamAskerBean> askerBeans = null;


    public TeamRequestModel(){

    }

    /**
     * 发送好友请求
     * @param id
     * @return 是否成功
     */
    public int addTeam(int id) {
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/team/sendJoinRequest", "uId=" +
                    AccountModel.getInstance().currentUser.id +
                    "&tId=" +
                    id);
            Msg msg = new Gson().fromJson(temp, Msg.class);
            return msg.code;
        }
        catch(Exception e){
            return App.NET_FAil;
        }
    }

    public void addAsker(TeamAskerBean bean){
        if (askerBeans == null)
            askerBeans = new ArrayList<>();
        askerBeans.add(bean);
    }


//    /**
//     * 发送好友请求
//     * @param userId
//     * @return 是否成功
//     */
//    public int addFriend(int userId) {
//        try{
//            String temp = NetVisitor.postNormal(App.host + "Talk/friend/request", "sendId=" +
//                    AccountModel.getInstance().currentUser.id +
//                    "&recvId=" +
//                    userId);
//            Msg msg = new Gson().fromJson(temp, Msg.class);
//            return msg.code;
//        }
//        catch(Exception e){
//            return -1;
//        }
//    }
//
    /**
     * 加载全部团队请求
     * @return 请求结果
     */
    public int loadRequest(){
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/team/getJoinRequest", "uId=" + AccountModel.getInstance().currentUser.id);
            MsgTeamAsker msg = new Gson().fromJson(temp, MsgTeamAsker.class);
            askerBeans = msg.data;
            return msg.code;
        }
        catch(Exception e){
            return -1;
        }
    }

    /**
     * 同意请求
     * @return 请求结果
     */
    public int agree(int position){
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/team/agree", "uId=" +
                    askerBeans.get(position).uid +
                    "&tId=" +
                    askerBeans.get(position).tid);
            Msg msg = new Gson().fromJson(temp, Msg.class);
            if (msg.code == App.NET_SUCCEED)
                askerBeans.remove(position);
            return msg.code;
        }
        catch(Exception e){
            return -1;
        }
    }

    /**
     * 拒绝请求
     * @return 请求结果
     */
    public int refuse(int position){
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/team/refuse", "uId=" +
                    askerBeans.get(position).uid +
                    "&tId=" +
                    askerBeans.get(position).tid);
            Msg msg = new Gson().fromJson(temp, Msg.class);
            if (msg.code == App.NET_SUCCEED)
                askerBeans.remove(position);
            return msg.code;
        }
        catch(Exception e){
            return -1;
        }
    }


    public boolean isWarn(){
        if (askerBeans != null && askerBeans.size() > 0)
            return true;
        else
            return false;
    }
//
//
//    /**
//     * 同意请求
//     * @param position
//     * @return 是否发送成功
//     */
//    public int AgreeRequest(int position){
//        try{
//            String temp = NetVisitor.postNormal(App.host + "Talk/friend/agree", "sendId=" +
//                    addFriendBeans.get(position).sendId +
//                    "&recvId=" +
//                    addFriendBeans.get(position).recvId);
//            Msg msg = new Gson().fromJson(temp, Msg.class);
//            FriendModel.getInstance().linkFriends.add(new FriendBean(addFriendBeans.get(position).sendId, addFriendBeans.get(position).sendNickname, addFriendBeans.get(position).sendHeadImgUrl, addFriendBeans.get(position).sendNickname));
//            //addFriendBeans.remove(position);
//            MyLocalObject.saveObject("linkFriends", FriendModel.getInstance().linkFriends);
//            return msg.code;
//        }
//        catch(Exception e){}
//        return -1;
//    }
//
//    /**
//     * 拒绝好友请求
//     * @param position
//     * @return
//     */
//    public int DisAgreeRequest(int position){
//        try{
//            String temp = NetVisitor.postNormal(App.host + "Talk/friend/disagree", "sendId=" +
//                    addFriendBeans.get(position).sendId +
//                    "&recvId=" +
//                    addFriendBeans.get(position).recvId);
//            Msg msg = new Gson().fromJson(temp, Msg.class);
//            addFriendBeans.remove(position);
//            return msg.code;
//        }
//        catch(Exception e){}
//        return -1;
//    }
}
