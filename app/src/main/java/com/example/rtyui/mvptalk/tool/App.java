package com.example.rtyui.mvptalk.tool;

import android.accounts.Account;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;

import com.avos.avoscloud.AVOSCloud;
import com.example.rtyui.mvptalk.Msg.Msg;
import com.example.rtyui.mvptalk.bean.UserBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;

/**
 * Created by rtyui on 2018/4/25.
 */

public class App extends Application {


    public static final int NET_FAil = -1;
    public static final int NET_SUCCEED = 2;

    public static String HOST_IP = "39.105.39.185";
    public static String host = "http://" + HOST_IP + ":80/";
    public static final String RECV_CHAT_ACTION = "RECV_CHAT_ACTION";
    public static final String STATU_CHAT_ACTION = "STATU_CHAT_ACTION";

    public static final String SEND_CHAT_ACTION = "SEND_CHAT_ACTION";//聊天消息
    public static final String SEND_ADD_FRIEND_ACTION = "SEND_ADD_FRIEND_ACTION";//好友处理消息
    public static final String RECV_ADD_FRIEND_ACTION = "RECV_ADD_FRIEND_ACTION";//好友处理消息
    public static final String DESTORY_PIPE = "DESTORY_PIPE";

    public static final String LOGIN_ACTION = "LOGIN_ACTION";
    public static final String EXIT_ACTION = "EXIT_ACTION";

    public static final int LINK_FRIEND_RESPONSE_NO_ONE = 100000;//无此人
    public static final int LINK_FRIEND_RESPONSE_HAD = 100001;//已经是好友
    public static final int LINK_FRIEND_RESPONSE_SEND_OK = 100002;//请求成功
    public static final String LINK_FRIEND_RESPONSE_RECV_OK = "LINK_FRIEND_RESPONSE_RECV_OK";//同意请求
    public static final int LINK_FRIEND_RESPONSE_RECV_REFUSE = 100004;//拒绝请求

    public static final String GET_ADD_FRIEND_REQUEST = "GET_ADD_FRIEND_REQUEST";

    public static final int NO_BROADCAST = -1;
    public static final int MAIN_BROADCAST = 1;
    public static final int TALK_BROADCAST = 2;

    public static final int MSG_SEND_GOOD = 99999;
    public static final int MSG_SEND_ING = 100000;
    public static final int MSG_SEND_BAD = 100001;

    public static int BROADCAST_STATU = NO_BROADCAST;

    public static int CURRENT_TALK = -1;

    public static Context context;

    public static final long ACCOUNT_TIME = 1000 * 60 * 60 * 24 * 7;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //初始化model
        AccountModel.getInstance();
        FriendModel.getInstance();
        MsgModel.getInstance();

        //解决截图问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        //初始化文件存储
        AVOSCloud.initialize(this,"X6aWf1qqQOkrgD1SvqKBLGmq-gzGzoHsz","fKEMg2bN07e2xxdIAxqQzd8S");
    }
}
