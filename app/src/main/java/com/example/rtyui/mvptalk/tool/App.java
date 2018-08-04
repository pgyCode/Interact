package com.example.rtyui.mvptalk.tool;

import android.accounts.Account;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;
import com.example.rtyui.mvptalk.Msg.Msg;
import com.example.rtyui.mvptalk.bean.UserBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.model.TeamRequestModel;
import com.example.rtyui.mvptalk.view.main.MainActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
    public static final String SEND_ACTION = "SEND_ACTION";
    public static final String FRIEND_ACTION = "FRIEND_ACTION";
    public static final String TEAM_ACTION = "TEAM_ACTION";
    public static final String ONLINE_STATU_ACTION = "ONLINE_STATU_ACTION";
    public static final String ONLINE_ACTION = "ONLINE_ACTION";
    public static final String SOCKET_LOGIN_ACTION = "SOCKET_LOGIN_ACTION";
    public static final String DEL_FRIEND_T_ACTION = "DEL_FRIEND_T_ACTION";
    public static final String DEL_FRIEND_F_ACTION = "DEL_FRIEND_F_ACTION";
    public static final String ADD_FRIEND_T_ACTION = "ADD_FRIEND_T_ACTION";
    public static final String ADD_FRIEND_F_ACTION = "ADD_FRIEND_F_ACTION";
    public static final String ADD_TEAM_T_ACTION = "ADD_TEAM_T_ACTION";
    public static final String ADD_TEAM_F_ACTION = "ADD_TEAM_F_ACTION";

    public static final String C2S_FRIEND_CHAT = "C2S_FRIEND_CHAT";
    public static final String C2S_FRIEND_CHAT_SURE = "C2S_FRIEND_CHAT_SURE";
    public static final String C2S_TEAM_CHAT = "C2S_TEAM_CHAT";
    public static final String C2S_TEAM_CHAT_SURE = "C2S_TEAM_CHAT_SURE";
    public static final String C2S_LOGIN = "C2S_LOGIN";
    public static final String C2S_LOGIN_SURE = "C2S_LOGIN_SURE";
    public static final String C2S_DEL_FRIEND = "C2S_DEL_FRIEND";
    public static final String C2S_DEL_FRIEND_SURE = "C2S_DEL_FRIEND_SURE";
    public static final String C2S_ADD_FRIEND = "C2S_ADD_FRIEND";
    public static final String C2S_ADD_FRIEND_SURE = "C2S_ADD_FRIEND_SURE";
    public static final String C2S_ADD_TEAM = "C2S_ADD_TEAM";
    public static final String C2S_ADD_TEAM_SURE = "C2S_ADD_TEAM_SURE";

    public static final String C2S_AGREE_FRIEND = "C2S_AGREE_FRIEND";
    public static final String C2S_AGREE_TEAM = "C2S_AGREE_TEAM";
    public static final String S2C_AGREE_FRIEND = "S2C_AGREE_FRIEND";
    public static final String S2C_AGREE_TEAM = "S2C_AGREE_TEAM";

    public static final String S2C_FRIEND_CHAT = "S2C_FRIEND_CHAT";//好友消息
    public static final String S2C_FRIEND_CHAT_SURE = "S2C_FRIEND_CHAT_SURE";//好友消息
    public static final String S2C_TEAM_CHAT = "S2C_TEAM_CHAT";//团队消息
    public static final String S2C_TEAM_CHAT_SURE = "S2C_TEAM_CHAT_SURE";//团队消息
    public static final String S2C_LOGIN = "S2C_LOGIN";//登陆消息
    public static final String S2C_LOGIN_SURE = "S2C_LOGIN_SURE";//登陆消息确认
    public static final String S2C_DEL_FRIEND = "S2C_DEL_FRIEND";
    public static final String S2C_DEL_FRIEND_SURE = "S2C_DEL_FRIEND_SURE";
    public static final String S2C_ADD_FRIEND = "S2C_ADD_FRIEND";
    public static final String S2C_ADD_FRIEND_SURE = "S2C_ADD_FRIEND_SURE";
    public static final String S2C_ADD_TEAM = "S2C_ADD_TEAM";
    public static final String S2C_ADD_TEAM_SURE = "S2C_ADD_TEAM_SURE";

    public static final String SEND_TEAM_CHAT_ACTION = "SEND_TEAM_CHAT_ACTION";//群组聊天消息
    public static final String SEND_CHAT_ACTION = "SEND_CHAT_ACTION";//聊天消息
    public static final String SEND_ADD_FRIEND_ACTION = "SEND_ADD_FRIEND_ACTION";//好友处理消息
    public static final String SEND_ADD_TEAM_ACTION = "SEND_ADD_TEAM_ACTION";//好友处理消息
    public static final String RECV_ADD_FRIEND_ACTION = "RECV_ADD_FRIEND_ACTION";//好友处理消息


    public static final String CLIENT_SURE_ACTION = "CLIENT_SURE_ACTION";//客户端确认消息
    public static final String SERVER_SURE_ACTION_CHAT = "SERVER_SURE_ACTION_CHAT";//服务器确认消息收到

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
    public static final String GET_ADD_TEAM_REQUEST = "GET_ADD_TEAM_REQUEST";

    public static final int NO_BROADCAST = -1;
    public static final int MAIN_BROADCAST = 1;
    public static final int TALK_BROADCAST = 2;

    public static final int MSG_SEND_GOOD = 99999;
    public static final int MSG_SEND_ING = 100000;
    public static final int MSG_SEND_BAD = 100001;


    public static final int MSG_READ = 100001;
    public static final int MSG_UNREAD = 100002;


    public static final int CHOOSE_IMG_INTENT = 100001;
    public static final int CHOOSE_FILE_INTENT = 100002;

    public static final int CATEGORY_FRIEND = 100001;
    public static final int CATEGORY_TEAM = 100002;

    public static int BROADCAST_STATU = NO_BROADCAST;

    public static int CURRENT_TALK = -1;

    public static Context context;

    public static final long ACCOUNT_TIME = 1000 * 60 * 60 * 24 * 7;


    //聊天界面时间显示间隔
    public static final long TALK_TIME_SPACE = 1000 * 60 * 1;

    public static final String LOCAL_IMG_PATH = Environment.getExternalStorageDirectory().getPath() + "/interact/imgDownload/";
    public static final String LOCAL_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/interact/fileDownload/";

    private ContinueRecvBroadcastReceiver continueRecvBroadcastReceiver;

    public static LocalBroadcastManager localBroadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

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
        intentFilter.addAction(App.GET_ADD_TEAM_REQUEST);
        intentFilter.addAction(App.STATU_CHAT_ACTION);
        intentFilter.addAction(App.DEL_FRIEND_T_ACTION);
        intentFilter.addAction(App.DEL_FRIEND_F_ACTION);
        intentFilter.addAction(App.SOCKET_LOGIN_ACTION);
        intentFilter.addAction(App.LINK_FRIEND_RESPONSE_RECV_OK);
        intentFilter.addAction(App.RECV_TEAM_CHAT_ACTION);
        intentFilter.addAction(App.ONLINE_STATU_ACTION);
        intentFilter.addAction(App.ONLINE_ACTION);
        intentFilter.addAction(App.FRIEND_ACTION);
        intentFilter.addAction(App.TEAM_ACTION);
        localBroadcastManager.registerReceiver(continueRecvBroadcastReceiver, intentFilter);


        /**
         * 网络状态监听注册
         */
        NetworkChangedReceiver networkChangedReceiver = new NetworkChangedReceiver();
        IntentFilter intentFilter1 = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangedReceiver, intentFilter1);
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
                case App.GET_ADD_TEAM_REQUEST:
                    TeamRequestModel.getInstance().actListeners();
                    break;
                case App.LINK_FRIEND_RESPONSE_RECV_OK:
                    FriendModel.getInstance().actListeners();
                    break;
                case App.RECV_TEAM_CHAT_ACTION:
                    MsgModel.getInstance().actListeners();
                    break;
                case App.SOCKET_LOGIN_ACTION:
                    Toast.makeText(context, "连接到socket服务器", Toast.LENGTH_SHORT).show();
                    break;
                case App.DEL_FRIEND_T_ACTION:
                    FriendModel.getInstance().actListeners();
                    MsgModel.getInstance().actListeners();
                    Toast.makeText(context, "删除好友成功", Toast.LENGTH_SHORT).show();
                    break;
                case App.DEL_FRIEND_F_ACTION:
                    Toast.makeText(context, "删除好友失败", Toast.LENGTH_SHORT).show();
                    break;
                case App.ADD_FRIEND_T_ACTION:
                    Toast.makeText(context, "申请好友成功", Toast.LENGTH_SHORT).show();
                    break;
                case App.ADD_FRIEND_F_ACTION:
                    Toast.makeText(context, "申请好友失败", Toast.LENGTH_SHORT).show();
                    break;
                case App.ADD_TEAM_T_ACTION:
                    Toast.makeText(context, "申请团队成功", Toast.LENGTH_SHORT).show();
                    break;
                case App.ADD_TEAM_F_ACTION:
                    Toast.makeText(context, "申请团队失败", Toast.LENGTH_SHORT).show();
                    break;
                case App.ONLINE_STATU_ACTION:
                    AccountModel.getInstance().actListeners();
                    break;
                case App.ONLINE_ACTION:
                    new NetTaskCode(new NetTaskCodeListener() {
                        @Override
                        public void before() {

                        }

                        @Override
                        public int middle() {
                            MsgModel.getInstance().doFlush();
                            FriendModel.getInstance().flush();
                            TeamModel.getInstance().NET_flushTeam();
                            TeamRequestModel.getInstance().loadRequest();
                            RequestModel.getInstance().loadRequest();
                            return App.NET_SUCCEED;
                        }

                        @Override
                        public void after(int code) {
                            MsgModel.getInstance().actListeners();
                            MsgModel.getInstance().actListeners();
                            FriendModel.getInstance().actListeners();
                            TeamModel.getInstance().actListeners();
                            TeamRequestModel.getInstance().actListeners();
                            RequestModel.getInstance().actListeners();
                        }
                    }).execute();
                    break;
                case App.FRIEND_ACTION:
                    new NetTaskCode(new NetTaskCodeListener() {
                        @Override
                        public void before() {

                        }

                        @Override
                        public int middle() {
                            FriendModel.getInstance().flush();
                            return App.NET_SUCCEED;
                        }

                        @Override
                        public void after(int code) {
                            FriendModel.getInstance().actListeners();
                        }
                    }).execute();
                    break;
                case App.TEAM_ACTION:
                    new NetTaskCode(new NetTaskCodeListener() {
                        @Override
                        public void before() {

                        }

                        @Override
                        public int middle() {
                            TeamModel.getInstance().NET_flushTeam();
                            return App.NET_SUCCEED;
                        }

                        @Override
                        public void after(int code) {
                            TeamModel.getInstance().actListeners();
                        }
                    }).execute();
                    break;
            }
        }






//        /**
//         * 取消广播注册
//         */
//        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
//        localBroadcastManager.unregisterReceiver(continueRecvBroadcastReceiver);

    }

    public class NetworkChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int netWorkStates = NetworkUtil.getNetWorkStates(context);

            switch (netWorkStates) {
                case NetworkUtil.TYPE_NONE:
                    //断网了
//                    Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
                case NetworkUtil.TYPE_MOBILE:
//                    Toast.makeText(context, "你正在使用移动网络", Toast.LENGTH_SHORT).show();
//                    //打开了移动网络
//                    new NetTaskCode(new NetTaskCodeListener() {
//                        @Override
//                        public void before() {
//
//                        }
//
//                        @Override
//                        public int middle() {
//                            return MsgModel.getInstance().doFlush();
//                        }
//
//                        @Override
//                        public void after(int code) {
//                            MsgModel.getInstance().actListeners();
//                        }
//                    }).execute();
                    break;
                case NetworkUtil.TYPE_WIFI:
//                    Toast.makeText(context, "你正在使用wifi网络", Toast.LENGTH_SHORT).show();
//                    //打开了WIFI
//                    new NetTaskCode(new NetTaskCodeListener() {
//                        @Override
//                        public void before() {
//
//                        }
//
//                        @Override
//                        public int middle() {
//                            return MsgModel.getInstance().doFlush();
//                        }
//
//                        @Override
//                        public void after(int code) {
//                            MsgModel.getInstance().actListeners();
//                        }
//                    }).execute();
                    break;

                default:
                    break;
            }
        }
    }


    public static void sendBroadCast(String s){
        Intent intent = new Intent(s);
        localBroadcastManager.sendBroadcast(intent);
    }

    public static void sendBroadCast(String action, String data){
        Intent intent = new Intent(action);
        intent.putExtra("data", data);
        localBroadcastManager.sendBroadcast(intent);
    }
}
