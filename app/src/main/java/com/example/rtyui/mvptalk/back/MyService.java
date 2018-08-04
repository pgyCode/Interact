package com.example.rtyui.mvptalk.back;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.example.rtyui.mvptalk.bean.ActionBean;
import com.example.rtyui.mvptalk.bean.AddFriendBean;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.bean.InfoActionBean;
import com.example.rtyui.mvptalk.bean.TeamChatBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.model.TeamRequestModel;
import com.example.rtyui.mvptalk.model.TempUserModel;
import com.example.rtyui.mvptalk.newBean.FriendBean;
import com.example.rtyui.mvptalk.newBean.Info;
import com.example.rtyui.mvptalk.newBean.TeamAskerBean;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.NotificationLinkFriendRecvUtils;
import com.example.rtyui.mvptalk.tool.NotificationLinkFriendUtils;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by rtyui on 2018/4/25.
 */

public class MyService extends Service {


    private List<Msger> msgers;

    Socket socket = null;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;

    private MyBroadcastReceiver myBroadcastReceiver = null;

    private ExecutorService sendMessager = null;
    private ExecutorService fixer = null;
    private ExecutorService verifier = null;

    private class Pipe implements Runnable{
        @Override
        public void run() {
            try {
                while (true) {
                    String str = dataInputStream.readUTF();
                    try{
                        Msger msger = new Gson().fromJson(str, Msger.class);
                        if (msger != null){
                            switch (msger.label){
                                case App.C2S_FRIEND_CHAT_SURE:
                                    deal_c2s_friend_chat_sure(msger.msg);
                                    break;
                                case App.C2S_TEAM_CHAT_SURE:
                                    deal_c2s_team_chat_sure(msger.msg);
                                    break;
                                case App.C2S_DEL_FRIEND_SURE:
                                    deal_c2s_del_friend_sure(msger.msg);
                                    break;
                                case App.C2S_ADD_FRIEND_SURE:
                                    deal_c2s_add_friend_sure(msger.msg);
                                    break;
                                case App.C2S_LOGIN_SURE:
                                    deal_c2s_login_sure();
                                    break;
                                case App.S2C_FRIEND_CHAT:
                                    dataOutputStream.writeUTF(new Msger(System.currentTimeMillis(), App.S2C_FRIEND_CHAT_SURE, msger.time + "").toString());
                                    deal_s2c_friend_chat(msger.msg);
                                    break;
                                case App.S2C_TEAM_CHAT:
                                    dataOutputStream.writeUTF(new Msger(System.currentTimeMillis(), App.S2C_TEAM_CHAT_SURE, msger.time + "").toString());
                                    deal_s2c_team_chat(msger.msg);
                                    break;
                                case App.S2C_DEL_FRIEND:
                                    dataOutputStream.writeUTF(new Msger(System.currentTimeMillis(), App.S2C_DEL_FRIEND_SURE, msger.time + "").toString());
                                    deal_s2c_del_friend(msger.msg);
                                    break;
                                case App.S2C_ADD_FRIEND:
                                    dataOutputStream.writeUTF(new Msger(System.currentTimeMillis(), App.S2C_ADD_FRIEND_SURE, msger.time + "").toString());
                                    deal_s2c_add_friend(msger.msg);
                                    break;
                                case App.S2C_ADD_TEAM:
                                    dataOutputStream.writeUTF(new Msger(System.currentTimeMillis(), App.S2C_ADD_TEAM_SURE, msger.time + "").toString());
                                    deal_s2c_add_team(msger.msg);
                                    break;
                                case App.S2C_AGREE_FRIEND:
                                    App.sendBroadCast(App.FRIEND_ACTION);
                                    break;
                                case App.S2C_AGREE_TEAM:
                                    App.sendBroadCast(App.TEAM_ACTION);
                                    break;
                            }
                        }
                    }catch(Exception e){
                        break;
                    }
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
                closeSocket();
            }
        }
    }

    private void connect() throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress("39.105.39.185", 8888), 1000);
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream.writeUTF(new Msger(System.currentTimeMillis(), App.C2S_LOGIN, AccountModel.getInstance().currentUser.id + "").toString());
        dataOutputStream.flush();
        App.sendBroadCast(App.ONLINE_ACTION);
    }

    private class Sender implements Runnable{

        private String msg;
        public Sender(String msg){
            this.msg = msg;
        }
        @Override
        public void run() {
            if (msg != null){
                try {
                    msgers.add(new Gson().fromJson(msg, Msger.class));
                    dataOutputStream.writeUTF(msg);
                    dataOutputStream.flush();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    closeSocket();
                }
            }
        }
    }

    public void closeSocket() {
        System.out.println("有没有执行");
        try {
            if (dataOutputStream != null) dataOutputStream.close();
            if (dataInputStream != null) dataInputStream.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private class Fix implements Runnable{

        @Override
        public void run() {
            while(true){
                try {
                    dataOutputStream.writeUTF("1");
                    System.out.println("------>心跳正常");
                    AccountModel.getInstance().onLine = true;
                    App.sendBroadCast(App.ONLINE_STATU_ACTION);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("------>心跳异常");
                    AccountModel.getInstance().onLine = false;
                    App.sendBroadCast(App.ONLINE_STATU_ACTION);
                    e.printStackTrace();
                    closeSocket();
                    try {
                        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        if(networkInfo == null || !networkInfo.isAvailable())
                        {
                            Thread.sleep(1000);
                        }
                        else
                        {
                            connect();
                            System.out.println("------>回复心跳 复活了");
                            sendMessager.execute(new Pipe());
                            AccountModel.getInstance().onLine = true;
                            App.sendBroadCast(App.ONLINE_STATU_ACTION);
                            Thread.sleep(1000);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        closeSocket();
                    }
                }
            }
        }
    }

    public class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case App.SEND_ACTION:
                    sendMessager.execute(new Sender(intent.getStringExtra("data")));
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(myBroadcastReceiver);
        closeSocket();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        msgers = new CopyOnWriteArrayList<>();
        verifier = Executors.newSingleThreadExecutor();
        verifier.execute(new MyVerifyRunnable());
        sendMessager = Executors.newFixedThreadPool(2);
        fixer = Executors.newSingleThreadExecutor();
        fixer.execute(new Fix());

        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter.addAction(App.SEND_ACTION);
        localBroadcastManager.registerReceiver(myBroadcastReceiver, intentFilter);
    }



    private void dealSendChatAction(String str){
        ChatBean chatBean = new Gson().fromJson(str, ChatBean.class);
        MsgModel.getInstance().add(chatBean);
        Intent intent = new Intent(App.RECV_CHAT_ACTION);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyService.this);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void dealSendAddFriendAction(String str){
        AddFriendBean bean = new Gson().fromJson(str, AddFriendBean.class);
        RequestModel.getInstance().addFriendBeans.add(bean);
        new NotificationLinkFriendUtils(MyService.this).sendNotification(bean);
        Intent intent = new Intent(App.GET_ADD_FRIEND_REQUEST);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyService.this);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void dealSendAddTeamAction(String str){
        AddFriendBean bean = new Gson().fromJson(str, AddFriendBean.class);
        RequestModel.getInstance().addFriendBeans.add(bean);
        new NotificationLinkFriendUtils(MyService.this).sendNotification(bean);
        Intent intent = new Intent(App.GET_ADD_FRIEND_REQUEST);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyService.this);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void dealRecvAddFriendAction(String str){
        AddFriendBean bean = new Gson().fromJson(str, AddFriendBean.class);
        FriendModel.getInstance().addOne(new FriendBean(bean.sendId, bean.sendNickname, bean.sendHeadImgUrl, bean.sendNickname));
        Intent intent = new Intent(App.LINK_FRIEND_RESPONSE_RECV_OK);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyService.this);
        localBroadcastManager.sendBroadcast(intent);
        new NotificationLinkFriendRecvUtils(MyService.this).sendNotification(bean);
    }

    private void dealSendTeamChatAction(String str){
        ChatBean bean = new Gson().fromJson(str, ChatBean.class);
        MsgModel.getInstance().add(bean);
        Intent intent = new Intent(App.RECV_TEAM_CHAT_ACTION);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyService.this);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void deal_c2s_friend_chat_sure(String str){
        long time = Long.parseLong(str);
        MsgModel.getInstance().changeStatu(time, App.MSG_SEND_GOOD);
        Intent intent = new Intent(App.STATU_CHAT_ACTION);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyService.this);
        localBroadcastManager.sendBroadcast(intent);
        msgerRemove(time);
    }
    private void deal_c2s_team_chat_sure(String str){
        long time = Long.parseLong(str);
        MsgModel.getInstance().changeStatu(time, App.MSG_SEND_GOOD);
        Intent intent = new Intent(App.STATU_CHAT_ACTION);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyService.this);
        localBroadcastManager.sendBroadcast(intent);
        msgerRemove(time);
    }

    private void deal_c2s_login_sure(){
        Intent intent = new Intent(App.SOCKET_LOGIN_ACTION);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyService.this);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void deal_s2c_friend_chat(String str){
        ChatBean chatBean = new Gson().fromJson(str, ChatBean.class);
        MsgModel.getInstance().add(chatBean);
        Intent intent = new Intent(App.RECV_CHAT_ACTION);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyService.this);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void deal_s2c_team_chat(String str){
        TeamChatBean chatBean = new Gson().fromJson(str, TeamChatBean.class);
        TempUserModel.getInstance().add(chatBean);
        MsgModel.getInstance().add(new ChatBean(chatBean.recvId, chatBean.sendId, chatBean.msg, chatBean.time, chatBean.category));
        Intent intent = new Intent(App.RECV_CHAT_ACTION);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyService.this);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void deal_c2s_del_friend_sure(String str){
        long time = Long.parseLong(str);
        Msger msger = msgerGet(time);
        ActionBean actionBean = new Gson().fromJson(msger.msg, ActionBean.class);
        FriendModel.getInstance().delFriendFromLocal(actionBean.recvId);
        msgers.remove(msger);
        App.sendBroadCast(App.DEL_FRIEND_T_ACTION);
    }

    private void deal_c2s_add_friend_sure(String str){
        long time = Long.parseLong(str);
        msgerRemove(time);
        App.sendBroadCast(App.ADD_FRIEND_T_ACTION);
    }

    private void deal_s2c_del_friend(String str){
        ActionBean actionBean = new Gson().fromJson(str, ActionBean.class);
        FriendModel.getInstance().delFriendFromLocal(actionBean.sendId);
        App.sendBroadCast(App.DEL_FRIEND_T_ACTION);
    }

    private void deal_s2c_add_friend(String str){
        InfoActionBean actionBean = new Gson().fromJson(str, InfoActionBean.class);
        RequestModel.getInstance().addFriendBeans.add(new AddFriendBean(actionBean.sendId, actionBean.name, actionBean.img, actionBean.recvId));
        App.sendBroadCast(App.GET_ADD_FRIEND_REQUEST);
    }

    private void deal_s2c_add_team(String str){
        TeamAskerBean actionBean = new Gson().fromJson(str, TeamAskerBean.class);
        TeamRequestModel.getInstance().addAsker(actionBean);
        App.sendBroadCast(App.GET_ADD_TEAM_REQUEST);
    }


    public class MyVerifyRunnable implements Runnable {
        @Override
        public void run() {
            while (true){
                boolean isMsgStatu = false;
                long time = System.currentTimeMillis();
                for (Msger msger : msgers){
                    if (time - msger.time > 3000) {
                        switch (msger.label){
                            case App.C2S_FRIEND_CHAT:
                                MsgModel.getInstance().changeStatu(msger.time, App.MSG_SEND_BAD);
                                isMsgStatu = true;
                                break;
                            case App.C2S_DEL_FRIEND:
                                App.sendBroadCast(App.DEL_FRIEND_F_ACTION);
                                break;
                            case App.C2S_ADD_FRIEND:
                                App.sendBroadCast(App.ADD_FRIEND_F_ACTION);
                                break;
                        }
                        msgers.remove(msger);
                    }
                }
                if (isMsgStatu){
                    App.sendBroadCast(App.STATU_CHAT_ACTION);
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public synchronized void msgerRemove(long time){
        for (Msger msger : msgers){
            if (msger.time == time) {
                msgers.remove(msger);
            }
        }
    }
    public synchronized Msger msgerGet(long time){
        for (Msger msger : msgers){
            if (msger.time == time) {
                return msger;
            }
        }
        return null;
    }
}
