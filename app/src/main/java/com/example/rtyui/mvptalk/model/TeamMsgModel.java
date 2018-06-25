package com.example.rtyui.mvptalk.model;

import com.example.rtyui.mvptalk.Msg.MsgCom;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.bean.ComBean;
import com.example.rtyui.mvptalk.newBean.TeamChatBean;
import com.example.rtyui.mvptalk.newBean.TeamComBean;
import com.example.rtyui.mvptalk.parent.Model;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MySqliteHelper;
import com.example.rtyui.mvptalk.tool.NetVisitor;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by rtyui on 2018/5/2.
 */

public class TeamMsgModel extends Model {

    private static TeamMsgModel instance;

    public static TeamMsgModel getInstance() {
        if (instance == null)
            instance = new TeamMsgModel();
        return instance;
    }
    public static synchronized void dstroyInstance() {
        if (instance != null) instance = null;
    }

    private TeamMsgModel(){
    }


    public void init(){
        comBeans = new LinkedList<>();
        if (AccountModel.getInstance().currentUser != null){
            List<TeamChatBean> chatBeans = MySqliteHelper.getInstance().getAll(TeamChatBean.class);
            for (TeamChatBean chatBean : chatBeans){
                flush(chatBean);
            }
        }
    }

    public List<TeamComBean> comBeans;

    /**
     * 辅助函数 添加未读取的消息
     * @param chatBean
     */
    public void addUnread(TeamChatBean chatBean){
        add(chatBean).unread++;
    }

    /**
     * 辅助函数 向combean添加chatBean
     * @param chatBean
     * @return
     */
    public TeamComBean add(TeamChatBean chatBean){
        MySqliteHelper.getInstance().insert(chatBean);
        return flush(chatBean);
    }

    /**
     * 辅助函数 具体添加
     * @param chatBean
     * @return
     */
    public TeamComBean flush(TeamChatBean chatBean){
        if (comBeans == null)
            comBeans = new LinkedList<>();
        int id = chatBean.recvId;
        TeamComBean comBean = null;
        for (int i = 0; i < comBeans.size(); i++){
            if (comBeans.get(i).id == id){
                comBean = comBeans.get(i);
            }
        }
        if (comBean == null){
            List<TeamChatBean> chatBeans = new LinkedList<>();
            comBean = new TeamComBean(id, chatBeans);
            comBeans.add(comBean);
        }
        comBean.chats.add(chatBean);
        return comBean;
    }

    /**
     * 请求自己未收到的消息
     * @return 请求结果
     */
    public int doFlush() {
        try{
//            String temp = NetVisitor.postNormal(App.host + "Talk/msg/getUnrecvMsg", "id=" +
//                    AccountModel.getInstance().currentUser.id);
//            temp = temp.replace("NickName", "Nickname").replace("sendDate", "time");
//            List<TeamChatBean> chatBeans1 = new Gson().fromJson(temp, MsgCom.class).data;
//            for (TeamChatBean chatBean : chatBeans1){
//                addUnread(chatBean);
//            }
            return App.NET_SUCCEED;
        }catch(Exception e){
            return App.NET_FAil;
        }
    }

    /**
     * 辅助函数 从id得到comBean
     * @param id
     * @return
     */
    public TeamComBean getCombeanById(int id){
        for (int i = 0; i < comBeans.size(); i++){
            if (comBeans.get(i).id == id)
                return comBeans.get(i);
        }
        return null;
    }


    /**
     * 辅助函数 获取未读取的总数
     * @return 返回总数
     */
    public int getUnread(){
        int sum = 0;
        for (int i = 0; i < comBeans.size(); i++){
            sum += comBeans.get(i).unread;
        }
        return sum;
    }

    //更改消息状态
    public void changeStatu(long time,int statu){
        MySqliteHelper.getInstance().update(TeamChatBean.class, " statu =" + statu, " time =" + time );
        for (int i = 0; i < comBeans.size(); i++){
            for (int j = 0; j < comBeans.get(i).chats.size(); j++){
                if (comBeans.get(i).chats.get(j).time == time)
                    comBeans.get(i).chats.get(j).statu = statu;
            }
        }
    }


    //得到真实位置对应的图片位置
    public int getImgPosition(int truePosition, int id){
        int count = -1;
        for (int i = 0; i < comBeans.size(); i++){
            if (id == comBeans.get(i).id){
                for (int j = 0; j <= truePosition; j++){
                    if (comBeans.get(i).chats.get(j).msg.startsWith(App.MSG_IMG))
                        count++;
                }
                return count;
            }
        }
        return -1;
    }

    //得到图片位置对应的真实位置
    public int getMsgPosition(int id, int imgPosition){
        int count = -1;
        for (int i = 0; i < comBeans.size(); i++){
            if (id == comBeans.get(i).id){
                for (int j = 0; j <= comBeans.get(i).chats.size(); j++){
                    if (comBeans.get(i).chats.get(j).msg.startsWith(App.MSG_IMG)){
                        count++;
                        if (count == imgPosition)
                            return j;
                    }
                }
            }
        }
        return -1;
    }

    //得到图片数量
    public int getImgCount(int id){
        int count = 0;
        for (int i = 0; i < comBeans.size(); i++){
            if (id == comBeans.get(i).id){
                for (int j = 0; j < comBeans.get(i).chats.size(); j++){
                    if (comBeans.get(i).chats.get(j).msg.startsWith(App.MSG_IMG))
                        count++;
                }
                return count;
            }
        }
        return -1;
    }

    /**
     * 帮助获取记录大小
     * @return
     */
    public int LOCAL_getSize(){
        if (comBeans == null)
            return 0;
        else
            return comBeans.size();
    }
}
