package com.example.rtyui.mvptalk.model;

import com.example.rtyui.mvptalk.Msg.MsgCom;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.bean.ComBean;
import com.example.rtyui.mvptalk.parent.Model;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MySqliteHelper;
import com.example.rtyui.mvptalk.tool.NetVisitor;
import com.google.gson.Gson;

import java.nio.file.attribute.PosixFileAttributes;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by rtyui on 2018/5/2.
 */

public class MsgModel extends Model {

    private static MsgModel instance;

    public static MsgModel getInstance() {
        if (instance == null)
            instance = new MsgModel();
        return instance;
    }
    public static synchronized void dstroyInstance() {
        if (instance != null) instance = null;
    }

    private MsgModel(){
    }


    public void init(){
        comBeans = new LinkedList<>();
        if (AccountModel.getInstance().currentUser != null){
            List<ChatBean> chatBeans = MySqliteHelper.getInstance().getAll(ChatBean.class);
            for (ChatBean chatBean : chatBeans){
                flush(chatBean);
            }
        }
    }

    public List<ComBean> comBeans;

    /**
     * 辅助函数 添加未读取的消息
     * @param chatBean
     */
    public void addUnread(ChatBean chatBean){
        add(chatBean).unread++;
    }

    /**
     * 辅助函数 向combean添加chatBean
     * @param chatBean
     * @return
     */
    public ComBean add(ChatBean chatBean){
        MySqliteHelper.getInstance().insert(chatBean);
        return flush(chatBean);
    }

    /**
     * 辅助函数 具体添加
     * @param chatBean
     * @return
     */
    public ComBean flush(ChatBean chatBean){
        int id = chatBean.sendId == AccountModel.getInstance().currentUser.id ? chatBean.recvId : chatBean.sendId;
        ComBean comBean = null;
        for (int i = 0; i < comBeans.size(); i++){
            if (comBeans.get(i).userId == id){
                comBean = comBeans.get(i);
            }
        }
        if (comBean == null){
            List<ChatBean> chatBeans = new LinkedList<>();
            comBean = new ComBean(
                    chatBean.sendId == AccountModel.getInstance().currentUser.id ? chatBean.recvId : chatBean.sendId,
                    chatBean.sendNickname.equals(AccountModel.getInstance().currentUser.nickname) ? chatBean.recvNickname : chatBean.sendNickname,
                    chatBean.sendHeadImgUrl.equals(AccountModel.getInstance().currentUser.headImgUrl) ? chatBean.recvHeadImgUrl : chatBean.sendHeadImgUrl, chatBeans);
            comBeans.add(comBean);
        }
        comBean.chats.add(chatBean);
        comBean.time = chatBean.time;
        comBeans.remove(comBean);
        comBeans.add(0, comBean);
        return comBean;
    }

    /**
     * 请求自己未收到的消息
     * @return 请求结果
     */
    public int doFlush() {
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/msg/getUnrecvMsg", "id=" +
                    AccountModel.getInstance().currentUser.id);
            temp = temp.replace("NickName", "Nickname");
            List<ChatBean> chatBeans1 = new Gson().fromJson(temp, MsgCom.class).data;
            for (ChatBean chatBean : chatBeans1){
                addUnread(chatBean);
            }
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
    public ComBean getCombeanById(int id){
        for (int i = 0; i < comBeans.size(); i++){
            if (comBeans.get(i).userId == id)
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
        MySqliteHelper.getInstance().update(ChatBean.class, " statu =" + statu, " time =" + time );
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
            if (id == comBeans.get(i).userId){
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
            if (id == comBeans.get(i).userId){
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
            if (id == comBeans.get(i).userId){
                for (int j = 0; j < comBeans.get(i).chats.size(); j++){
                    if (comBeans.get(i).chats.get(j).msg.startsWith(App.MSG_IMG))
                        count++;
                }
                return count;
            }
        }
        return -1;
    }
}
