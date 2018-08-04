package com.example.rtyui.mvptalk.model;

import android.accounts.Account;

import com.example.rtyui.mvptalk.newBean.TeamBean;
import com.example.rtyui.mvptalk.newMsg.TeamCreateMsg;
import com.example.rtyui.mvptalk.newMsg.TeamFlushMsg;
import com.example.rtyui.mvptalk.parent.Model;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MyLocalObject;
import com.example.rtyui.mvptalk.tool.NetVisitor;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by rtyui on 2018/4/25.
 */

public class TeamModel extends Model {


    private static TeamModel instance;

    public static TeamModel getInstance() {
        if (instance == null)
            instance = new TeamModel();
        return instance;
    }

    public int CURRENT_TALK = -1;
    public static synchronized void dstroyInstance() {
        if (instance != null) instance = null;
    }

    /**
     * 外部功能：初始化获取自己的群组
     */
    public void OUTER_init(){
        if (AccountModel.getInstance().currentUser != null) {
            teamBeans = (List<TeamBean>) MyLocalObject.getObject("teams_" + AccountModel.getInstance().currentUser.id);
        }
    }

    /**
     * 网络功能：初始化刷新自己群组
     */
    public int NET_flushTeam(){
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/team/myTeam", "uId=" +
                    AccountModel.getInstance().currentUser.id);
            TeamFlushMsg msg = new Gson().fromJson(temp, TeamFlushMsg.class);
            if (msg.code == App.NET_SUCCEED){
                teamBeans = msg.data;
                MyLocalObject.saveObject("teams_" + AccountModel.getInstance().currentUser.id, teamBeans);
            }
            return msg.code;
        }catch(Exception e){
            return App.NET_FAil;
        }
    }

    public List<TeamBean> teamBeans = null;


    /**
     * 网络请求：创建群组
     * @param name
     * @return
     */
    public int NET_createTeam(String name){
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/team/createTeam", "uId=" +
                    AccountModel.getInstance().currentUser.id +
                    "&name=" +
                    name);
            TeamCreateMsg msg = new Gson().fromJson(temp, TeamCreateMsg.class);
            if (msg.code == App.NET_SUCCEED)
                INNER_addTeam(msg.data);
            return msg.code;
        }catch(Exception e){
            return App.NET_FAil;
        }
    }


    /**
     * 内部功能：添加bean
     * @param bean
     */
    private void INNER_addTeam(TeamBean bean) {
        if (teamBeans == null)
            teamBeans = new ArrayList<>();
        teamBeans.add(bean);
        MyLocalObject.saveObject("teams_" + AccountModel.getInstance().currentUser.id, teamBeans);
    }


    /**
     * 外部功能：通过id获取teambean
     * @param id
     * @return
     */
    public TeamBean OUTER_getTeamById(int id){
        for (TeamBean teamBean : teamBeans){
            if (teamBean.id == id)
                return teamBean;
        }
        return null;
    }



//    /**
//     * 外部功能：通过teamId, userId获取memberbean
//     * @param teamId
//     * @param userId
//     * @return
//     */
//    public TeamMemberBean OUTER_getMemberById(int teamId, int userId){
//        List<TeamMemberBean> members = OUTER_getTeamById(teamId).members;
//        for (TeamMemberBean bean : members){
//            if (bean.id == userId)
//                return bean;
//        }
//        return null;
//    }



//    /**
//     * 辅助函数
//     * @param id
//     * @return 对应id的用户
//     */
//    public FriendBean getUserById(int id) {
//        for (FriendBean friendBean : linkFriends) {
//            if (friendBean.id == id)
//                return friendBean;
//        }
//        return null;
//    }
//
//    /**
//     * 请求好友列表
//     * @return 请求结果
//     */
//    public int flush() {
//        try{
//            String temp = NetVisitor.postNormal(App.host + "Talk/friend/getAllFri?", "userId=" +
//                    AccountModel.getInstance().currentUser.id);
//            FriendFlushMsg msg = new Gson().fromJson(temp, FriendFlushMsg.class);
//            if (msg.code == App.NET_SUCCEED){
//                linkFriends = msg.data;
//                MyLocalObject.saveObject("linkFriends_" + AccountModel.getInstance().currentUser.id, linkFriends);
//            }
//            return msg.code;
//        }
//        catch(Exception e){
//            return App.NET_FAil;
//        }
//    }
//
//    /**
//     * 修改备注
//     * @param remark 备注
//     * @param id
//     * @return 请求结果
//     */
//    public int changeRemark(String remark, int id){
//        try{
//            String temp = NetVisitor.postNormal(App.host + "Talk/friend/updateRemark", "userId=" +
//                    AccountModel.getInstance().currentUser.id +
//                    "&guestId=" +
//                    id +
//                    "&remark=" +
//                    remark);
//            Msg msg = new Gson().fromJson(temp, Msg.class);
//            if (msg.code == App.NET_SUCCEED)
//            {
//                TeamModel.getInstance().getUserById(id).remark = remark;
//                MyLocalObject.saveObject("linkFriends_" + AccountModel.getInstance().currentUser.id, linkFriends);
//            }
//            return msg.code;
//        }
//        catch(Exception e){
//            return App.NET_FAil;
//        }
//    }
//
//    /**
//     * 删除好友
//     * @param id
//     * @return 请求结果
//     */
//    public int delFriend(int id){
//        try {
//            String temp = NetVisitor.postNormal(App.host + "Talk/friend/delFriend?", "userId=" +
//                    AccountModel.getInstance().currentUser.id +
//                    "&guestId" +
//                    "=" +
//                    id
//            );
//            Msg msg = new Gson().fromJson(temp, Msg.class);
//            if (msg.code == App.NET_SUCCEED)
//            {
//                linkFriends.remove(getUserById(id));
//                MsgModel.getInstance().comBeans.remove(MsgModel.getInstance().getCombeanById(id));
//                MySqliteHelper.getInstance().delete(ChatBean.class, "recvId=" +
//                        id +
//                        " or sendId=" +
//                        id);
//                MyLocalObject.saveObject("linkFriends_" + AccountModel.getInstance().currentUser.id, linkFriends);
//            }
//            return msg.code;
//        }catch (Exception e){
//            return App.NET_FAil;
//        }
//    }
//
//    /**
//     * 请求要添加的人信息
//     * @param id
//     * @return 请求返回内容
//     */
//    public String getFriend(int id){
//        try{
//            String temp = NetVisitor.postNormal(App.host + "Talk/friend/getInformation", "searchId=" +
//                    id);
//            return temp;
//        }
//        catch(Exception e){
//            return null;
//        }
//    }
}
