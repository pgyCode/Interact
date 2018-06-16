package com.example.rtyui.mvptalk.back;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.example.rtyui.mvptalk.Msg.Msg;
import com.example.rtyui.mvptalk.Msg.MsgChat;
import com.example.rtyui.mvptalk.bean.AddFriendBean;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.bean.LoginBean;
import com.example.rtyui.mvptalk.bean.UserBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.tool.AbstractNetTaskCode;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MySqliteHelper;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;
import com.example.rtyui.mvptalk.tool.NotificationLinkFriendRecvUtils;
import com.example.rtyui.mvptalk.tool.NotificationLinkFriendUtils;
import com.example.rtyui.mvptalk.tool.NotificationUtils;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by rtyui on 2018/4/25.
 */

public class MyService extends Service {

    Socket socket = null;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;

    private MyBroadcastReceiver myBroadcastReceiver = null;

    private ExecutorService sendMessager = null;
    private ExecutorService fixer = null;

    private class Pipe implements Runnable{
        @Override
        public void run() {
            try {
                while (true) {
                    String str = dataInputStream.readUTF();
                    String[] strings = str.split("[|]");
                    System.out.println(str + "消息");
                    switch (strings[0]){
                        case App.SEND_CHAT_ACTION:
                            dealSendChatAction(strings[1]);
                            break;
                        case App.SEND_ADD_FRIEND_ACTION:
                            dealSendAddFriendAction(strings[1]);
                            break;
                        case App.RECV_ADD_FRIEND_ACTION:
                            dealRecvAddFriendAction(strings[1]);
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
        dataOutputStream.writeUTF(App.LOGIN_ACTION + "|" + new Gson().toJson(new LoginBean(AccountModel.getInstance().currentUser.id)));
        dataOutputStream.flush();
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
        sendMessager = Executors.newFixedThreadPool(2);
        fixer = Executors.newSingleThreadExecutor();
        fixer.execute(new Fix());
        return super.onStartCommand(intent, flags, startId);
    }

    private class Fix implements Runnable{

        @Override
        public void run() {
            while(true){
                System.out.println("------>我正在尝试心跳");
                try {
                    dataOutputStream.writeUTF("1");
                    System.out.println("------>心跳正常");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("------>心跳异常");
                    e.printStackTrace();
                    try {
                        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        if(networkInfo == null || !networkInfo.isAvailable())
                        {
                            System.out.println("------>此次无法修复");
                            System.out.println("你没网啊，我睡一会儿");
                            Thread.sleep(1000);
                            System.out.println("我睡完了");
                        }
                        else
                        {
                            connect();
                            System.out.println("------>回复心跳 复活了");
                            sendMessager.execute(new Pipe());
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
            final String temp = intent.getStringExtra("data");
            switch (intent.getAction()){
                case App.SEND_CHAT_ACTION:
                    new NetTaskCode(new NetTaskCodeListener() {
                        @Override
                        public void before() {

                        }

                        @Override
                        public int middle() {
                            try {
                                dataOutputStream.writeUTF(App.SEND_CHAT_ACTION + "|" + temp);
                                dataOutputStream.flush();
                                MsgModel.getInstance().changeStatu(
                                        new Gson().fromJson(temp, ChatBean.class).time, App.MSG_SEND_GOOD);
                            } catch (IOException e) {
                                e.printStackTrace();
                                closeSocket();
                                MsgModel.getInstance().changeStatu(
                                        new Gson().fromJson(temp, ChatBean.class).time, App.MSG_SEND_BAD);
                            }
                            return 0;
                        }

                        @Override
                        public void after(int code) {
                            MsgModel.getInstance().actListeners();
                        }
                    }).executeOnExecutor(sendMessager);
                    break;
                case App.SEND_ADD_FRIEND_ACTION:
                    sendMessager.execute(new Sender(
                            App.SEND_ADD_FRIEND_ACTION + "|" + intent.getStringExtra("data")));
                    break;
                case App.RECV_ADD_FRIEND_ACTION:
                    sendMessager.execute(new Sender(
                            App.RECV_ADD_FRIEND_ACTION + "|" + intent.getStringExtra("data")));
                    break;
                case App.DESTORY_PIPE:
                    closeSocket();
                    MyService.this.stopSelf();
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

        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter.addAction(App.SEND_CHAT_ACTION);
        intentFilter.addAction(App.SEND_ADD_FRIEND_ACTION);
        intentFilter.addAction(App.RECV_ADD_FRIEND_ACTION);
        intentFilter.addAction(App.DESTORY_PIPE);
        localBroadcastManager.registerReceiver(myBroadcastReceiver, intentFilter);
    }



    private void dealSendChatAction(String str){
        ChatBean chatBean = new Gson().fromJson(str, ChatBean.class);

        if (App.CURRENT_TALK < 0){
            MsgModel.getInstance().addUnread(chatBean);
            new NotificationUtils(MyService.this).sendNotification(chatBean);
        }else{
            MsgModel.getInstance().flush(chatBean);
        }

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

    private void dealRecvAddFriendAction(String str){
        AddFriendBean bean = new Gson().fromJson(str, AddFriendBean.class);
        FriendModel.getInstance().linkFriends.add(new UserBean(bean.sendId, bean.sendNickname, bean.sendHeadImgUrl));
        Intent intent = new Intent(App.LINK_FRIEND_RESPONSE_RECV_OK);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyService.this);
        localBroadcastManager.sendBroadcast(intent);
        new NotificationLinkFriendRecvUtils(MyService.this).sendNotification(bean);
    }
}
