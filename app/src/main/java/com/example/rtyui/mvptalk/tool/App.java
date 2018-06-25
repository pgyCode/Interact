package com.example.rtyui.mvptalk.tool;

import android.accounts.Account;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;

import com.avos.avoscloud.AVOSCloud;
import com.example.rtyui.mvptalk.Msg.Msg;
import com.example.rtyui.mvptalk.bean.UserBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.model.TeamMsgModel;
import com.example.rtyui.mvptalk.view.main.MainActivity;

/**
 * Created by rtyui on 2018/4/25.
 */

public class App extends Application {


    public static final int NET_FAil = -1;
    public static final int NET_SUCCEED = 2;

    public static String HOST_IP = "39.105.39.185";
    public static String host = "http://" + HOST_IP + ":80/";
    public static final String RECV_CHAT_ACTION = "RECV_CHAT_ACTION";
    public static final String RECV_TEAM_CHAT_ACTION = "RECV_TEAM_CHAT_ACTION";
    public static final String STATU_CHAT_ACTION = "STATU_CHAT_ACTION";


    public static final String SEND_TEAM_CHAT_ACTION = "SEND_TEAM_CHAT_ACTION";//群组聊天消息
    public static final String SEND_CHAT_ACTION = "SEND_CHAT_ACTION";//聊天消息
    public static final String SEND_ADD_FRIEND_ACTION = "SEND_ADD_FRIEND_ACTION";//好友处理消息
    public static final String RECV_ADD_FRIEND_ACTION = "RECV_ADD_FRIEND_ACTION";//好友处理消息
    public static final String DESTORY_PIPE = "DESTORY_PIPE";

    public static final String LOGIN_ACTION = "LOGIN_ACTION";
    public static final String EXIT_ACTION = "EXIT_ACTION";


    public static final String MSG_CHAT = "MSG_CHAT:";
    public static final String MSG_IMG = "MSG_IMG:";

    public static final int PHOTO_CHOOSE_SIGN_SENDIMG = 100000;
    public static final int PHOTO_CHOOSE_SIGN_CUTIMG = 100001;
    public static final int PHOTO_CHOOSE_SIGN_TEAM_SENDIMG = 100002;

    public static final int PHOTO_SHOW_SIGN_SENDIMG = 100000;
    public static final int PHOTO_SHOW_SIGN_TEAM_SENDIMG = 100001;

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


    public static final int CHOOSE_IMG_INTENT = 100001;
    public static final int CHOOSE_FILE_INTENT = 100002;

    public static int BROADCAST_STATU = NO_BROADCAST;

    public static int CURRENT_TALK = -1;

    public static Context context;

    public static final long ACCOUNT_TIME = 1000 * 60 * 60 * 24 * 7;


    //聊天界面时间显示间隔
    public static final long TALK_TIME_SPACE = 1000 * 60 * 1;

    public static final String LOCAL_IMG_PATH = Environment.getExternalStorageDirectory().getPath() + "/interact/imgDownload/";
    public static final String LOCAL_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/interact/fileDownload/";

    private ContinueRecvBroadcastReceiver continueRecvBroadcastReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //解决截图问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        //初始化用户信息 为了自动登陆
        AccountModel.getInstance().init();

        //初始化文件存储
        AVOSCloud.initialize(this,"X6aWf1qqQOkrgD1SvqKBLGmq-gzGzoHsz","fKEMg2bN07e2xxdIAxqQzd8S");

        /**
         * 注册广播
         * 1.接受消息
         * 2.接受添加好友请求
         * 3.接受聊天状态改变
         * 4.接受好友同意添加
         * 5.加收团队聊天消息
         */
        continueRecvBroadcastReceiver = new ContinueRecvBroadcastReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(App.RECV_CHAT_ACTION);
        intentFilter.addAction(App.GET_ADD_FRIEND_REQUEST);
        intentFilter.addAction(App.STATU_CHAT_ACTION);
        intentFilter.addAction(App.LINK_FRIEND_RESPONSE_RECV_OK);
        intentFilter.addAction(App.RECV_TEAM_CHAT_ACTION);
        localBroadcastManager.registerReceiver(continueRecvBroadcastReceiver, intentFilter);
    }

    private class ContinueRecvBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case App.RECV_CHAT_ACTION:
                case App.STATU_CHAT_ACTION:
                    MsgModel.getInstance().actListeners();
                    break;
                case App.GET_ADD_FRIEND_REQUEST:
                    RequestModel.getInstance().actListeners();
                    break;
                case App.LINK_FRIEND_RESPONSE_RECV_OK:
                    FriendModel.getInstance().actListeners();
                    break;
                case App.RECV_TEAM_CHAT_ACTION:
                    TeamMsgModel.getInstance().actListeners();
                    break;
            }
        }




//        /**
//         * 取消广播注册
//         */
//        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
//        localBroadcastManager.unregisterReceiver(continueRecvBroadcastReceiver);

    }

}
