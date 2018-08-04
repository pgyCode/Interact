package com.example.rtyui.mvptalk.model;

import com.example.rtyui.mvptalk.Msg.MsgCom;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.bean.ComBean;
import com.example.rtyui.mvptalk.parent.Model;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.NetVisitor;
import com.example.rtyui.mvptalk.tool.NotificationUtils;
import com.google.gson.Gson;

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
                int id = chatBean.sendId == AccountModel.getInstance().currentUser.id ? chatBean.recvId : chatBean.sendId;
                if ((chatBean.category == App.CATEGORY_FRIEND && FriendModel.getInstance().getUserById(id) != null) ||
                        (chatBean.category == App.CATEGORY_TEAM && TeamModel.getInstance().OUTER_getTeamById(id) != null) )
                    flush(chatBean);
            }
        }
    }

    public List<ComBean> comBeans;
    public boolean isNotifi = false;

    /**
     * 辅助函数 添加未读取的消息
     * @param chatBean
     */
//    public void addUnread(ChatBean chatBean){
//        add(chatBean).unread++;
//    }

    /**
     * 辅助函数 向combean添加chatBean
     * @param chatBean
     * @return
     */
    public void add(ChatBean chatBean){
        int id = chatBean.sendId == AccountModel.getInstance().currentUser.id ? chatBean.recvId : chatBean.sendId;
        if ((chatBean.category == App.CATEGORY_FRIEND && FriendModel.getInstance().getUserById(id) != null) ||
                (chatBean.category == App.CATEGORY_TEAM && TeamModel.getInstance().OUTER_getTeamById(chatBean.recvId) != null) ){
            if ((chatBean.category == App.CATEGORY_FRIEND && FriendModel.getInstance().CURRENT_TALK == chatBean.sendId)
                    || (chatBean.category == App.CATEGORY_TEAM && TeamModel.getInstance().CURRENT_TALK == chatBean.recvId)
                    || AccountModel.getInstance().currentUser.id == chatBean.sendId){
                chatBean.read = App.MSG_READ;
                flush(chatBean);
            }else{
                chatBean.read = App.MSG_UNREAD;
                flush(chatBean);
                if (isNotifi){
                    new NotificationUtils(App.context).sendNotification(chatBean);
                }
            }
            MySqliteHelper.getInstance().insert(chatBean);
        }
    }

    /**
     * 辅助函数 具体添加
     * @param chatBean
     * @return
     */
    public ComBean flush(ChatBean chatBean){
        if (chatBean.category == App.CATEGORY_FRIEND){
            int id = chatBean.sendId == AccountModel.getInstance().currentUser.id ? chatBean.recvId : chatBean.sendId;
            ComBean comBean = null;
            for (int i = 0; i < comBeans.size(); i++){
                if (comBeans.get(i).id == id && comBeans.get(i).category == App.CATEGORY_FRIEND){
                    comBean = comBeans.get(i);
                }
            }
            if (comBean == null){
                List<ChatBean> chatBeans = new LinkedList<>();
                comBean = new ComBean(
                        id,
                        chatBean.category,
                        chatBeans);
                comBeans.add(comBean);
            }
            comBean.chats.add(chatBean);
            comBeans.remove(comBean);
            comBeans.add(0, comBean);
            return comBean;
        }else if (chatBean.category == App.CATEGORY_TEAM){
            int id = chatBean.recvId;
            ComBean comBean = null;
            for (int i = 0; i < comBeans.size(); i++){
                if (comBeans.get(i).id == id && comBeans.get(i).category == App.CATEGORY_TEAM){
                    comBean = comBeans.get(i);
                }
            }
            if (comBean == null){
                List<ChatBean> chatBeans = new LinkedList<>();
                comBean = new ComBean(
                        id,
                        chatBean.category,
                        chatBeans);
                comBeans.add(comBean);
            }
            comBean.chats.add(chatBean);
            comBeans.remove(comBean);
            comBeans.add(0, comBean);
            return comBean;
        }
        else
            return null;
    }

    /**
     * 请求自己未收到的消息
     * @return 请求结果
     */
    public int doFlush() {
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/msg/getUnrecvMsg", "owner=" +
                    AccountModel.getInstance().currentUser.id);
            List<ChatBean> chatBeans1 = new Gson().fromJson(temp, MsgCom.class).data;
            if (chatBeans1 != null){
                for (ChatBean chatBean : chatBeans1){
                    chatBean.time = System.currentTimeMillis();
                    int id = chatBean.sendId == AccountModel.getInstance().currentUser.id ? chatBean.recvId : chatBean.sendId;
                    if ((chatBean.category == App.CATEGORY_FRIEND && FriendModel.getInstance().getUserById(id) != null) ||
                            (chatBean.category == App.CATEGORY_TEAM && TeamModel.getInstance().OUTER_getTeamById(id) != null) )
                        add(chatBean);
                }
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
    public ComBean INNER_getCombeanById(int id){
        for (int i = 0; i < comBeans.size(); i++){
            if (comBeans.get(i).id == id)
                return comBeans.get(i);
        }
        return null;
    }

    /**
     * 辅助函数 从id得到comBean
     * @param id
     * @return
     */
    public ComBean getFriendCombeanById(int id){
        for (int i = 0; i < comBeans.size(); i++){
            if (comBeans.get(i).id == id && comBeans.get(i).category == App.CATEGORY_FRIEND)
                return comBeans.get(i);
        }
        return null;
    }

    /**
     * 辅助函数 从id得到comBean
     * @param id
     * @return
     */
    public ComBean getTeamCombeanById(int id){
        for (int i = 0; i < comBeans.size(); i++){
            if (comBeans.get(i).id == id  && comBeans.get(i).category == App.CATEGORY_TEAM)
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
            sum += getUnreadByPosition(i);
        }
        return sum;
    }


    /**
     * 辅助函数 获取未读取的总数
     * @return 返回总数
     */
    public int getUnreadByPosition(int position){
        int sum = 0;
        List<ChatBean> chatBeans = comBeans.get(position).chats;
        for (int i = 0; i < chatBeans.size(); i++){
            if (chatBeans.get(i).read == App.MSG_UNREAD)
                sum++;
        }
        return sum;
    }


    /**
     * 辅助函数 设置为已经读取
     * @return 返回总数
     */
    public void setFriendReadById(int id){
        ComBean comBean = MsgModel.getInstance().getFriendCombeanById(id);
        if (comBean != null) {
            List<ChatBean> chatBeans = comBean.chats;
            for (int i = 0; i < chatBeans.size(); i++) {
                if (chatBeans.get(i).read == App.MSG_UNREAD)
                    chatBeans.get(i).read = App.MSG_READ;
            }
        }
        MySqliteHelper.getInstance().update(ChatBean.class, " read = " + App.MSG_READ, "(sendId = " +
                id +
                " or recvId = " +
                id +
                ") and read = " +
                App.MSG_UNREAD);
    }

    /**
     * 辅助函数 设置为已经读取
     * @return 返回总数
     */
    public void setTeamReadById(int id){
        ComBean comBean = MsgModel.getInstance().getTeamCombeanById(id);
        if (comBean != null) {
            List<ChatBean> chatBeans = comBean.chats;
            for (int i = 0; i < chatBeans.size(); i++) {
                if (chatBeans.get(i).read == App.MSG_UNREAD)
                    chatBeans.get(i).read = App.MSG_READ;
            }
        }
        MySqliteHelper.getInstance().update(ChatBean.class, " read = " + App.MSG_READ, "(sendId = " +
                id +
                " or recvId = " +
                id +
                ") and read = " +
                App.MSG_UNREAD);
    }

    //更改消息状态
    public void changeStatu(long time,int statu){
        MySqliteHelper.getInstance().update(ChatBean.class, " statu =" + statu, " time =" + time );
        for (int i = 0; i < comBeans.size(); i++){
            for (int j = 0; j < comBeans.get(i).chats.size(); j++){
                if (comBeans.get(i).chats.get(j).time == time) {
                    comBeans.get(i).chats.get(j).statu = statu;
                    break;
                }
            }
        }
    }

    //更改消息状态
    public void setFailure(long time){
        System.out.println("我们都在这里");
        if (comBeans != null){
            MySqliteHelper.getInstance().update(ChatBean.class, " statu =" + App.MSG_SEND_BAD, "statu = " +
                    App.MSG_SEND_ING +
                    " and time <" + (time - 3000) );
            for (int i = 0; i < comBeans.size(); i++){
                for (int j = 0; j < comBeans.get(i).chats.size(); j++){
                    if (comBeans.get(i).chats.get(j).time < time - 3000 && comBeans.get(i).chats.get(j).statu == App.MSG_SEND_ING)
                        comBeans.get(i).chats.get(j).statu = App.MSG_SEND_BAD;
                }
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

    public void delFriendFromLocal(int id){
        MsgModel.getInstance().comBeans.remove(MsgModel.getInstance().getFriendCombeanById(id));
        MySqliteHelper.getInstance().delete(ChatBean.class, "recvId =" +
                id +
                " or sendId=" +
                id +
                " and category=" +
                App.CATEGORY_FRIEND);
    }
}
