package com.example.rtyui.mvptalk.view.main;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.back.MyService;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.bean.UserBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.model.TeamMsgModel;
import com.example.rtyui.mvptalk.newBean.TeamChatBean;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MyImgShow;
import com.example.rtyui.mvptalk.tool.MyLocalObject;
import com.example.rtyui.mvptalk.tool.MySqliteHelper;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;
import com.example.rtyui.mvptalk.view.friend.FriendFragment;
import com.example.rtyui.mvptalk.view.friend.NewFriendActivity;
import com.example.rtyui.mvptalk.view.msg.MsgFragment;
import com.example.rtyui.mvptalk.view.own.LeafFragment;
import com.example.rtyui.mvptalk.view.team.TeamFragment;

import java.lang.ref.WeakReference;

import cn.a527yxy.sideslipdemo.SideSlipLinearLayout;

/**
 * Created by rtyui on 2018/4/25.
 */

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private Fragment msgFrag = null;
    private Fragment friendFrag = null;
    private Fragment leafFrag = null;
    private Fragment teamFrag = null;

    private TextView txtTitle = null;
    private TextView txt_unread;

//    private ContinueRecvBroadcastReceiver continueRecvBroadcastReceiver;

    private OnModelChangeListener modelListener;

    private PowerManager.WakeLock wl;//状态锁

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /**
         * 创建聊天记录表
         * 1.创建个人聊天表
         * 2.创建团队聊天表
         */
        MySqliteHelper.getInstance().mkTable(ChatBean.class);
        MySqliteHelper.getInstance().mkTable(TeamChatBean.class);

        //启动服务
        Intent intent = new Intent(this, MyService.class);
        startService(intent);

        //初始化页面
        setContentView(R.layout.main);
        initLayout();

        //设置电量模式，使得锁屏是可以接受消息
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "talk:socket");
        wl.acquire();


    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void initLayout(){
        txtTitle = findViewById(R.id.txt_title);

        msgFrag = new MsgFragment();
        friendFrag = new FriendFragment();
        teamFrag = new TeamFragment();
        leafFrag = new LeafFragment();

        txt_unread = findViewById(R.id.txt_unread);

        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                int temp = MsgModel.getInstance().getUnread();
                if (temp == 0)
                    txt_unread.setVisibility(View.GONE);
                else{
                    txt_unread.setVisibility(View.VISIBLE);
                    txt_unread.setText(temp + "");
                }
                if (RequestModel.getInstance().addFriendBeans != null)
                    ((TextView)findViewById(R.id.txt_request)).setText(RequestModel.getInstance().addFriendBeans.size() + "");
            }
        };
        RequestModel.getInstance().listeners.add(modelListener);
        MsgModel.getInstance().listeners.add(modelListener);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .add(R.id.all, msgFrag)
                .add(R.id.all, friendFrag)
                .add(R.id.all, teamFrag)
                .add(R.id.all, leafFrag)
                .commit();
        findViewById(R.id.btn_msg).setOnClickListener(this);
        findViewById(R.id.btn_friend).setOnClickListener(this);
        findViewById(R.id.btn_team).setOnClickListener(this);
        findViewById(R.id.btn_own).setOnClickListener(this);

        findViewById(R.id.btn_new_friend).setOnClickListener(this);

        replaceFragment(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_msg:
                replaceFragment(0);
                break;
            case R.id.btn_friend:
                replaceFragment(1);
                break;
            case R.id.btn_team:
                replaceFragment(2);
                break;
            case R.id.btn_own:
                replaceFragment(3);
                break;
            case R.id.btn_new_friend:
                startActivity(new Intent(this, NewFriendActivity.class));
                break;
        }
    }


    private void replaceFragment(int sign){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (sign){
            case 0:
                fragmentTransaction
                        .show(msgFrag)
                        .hide(friendFrag)
                        .hide(teamFrag)
                        .hide(leafFrag)
                        .commit();
                txtTitle.setText("消息");
                ((ImageView)findViewById(R.id.img_chat)).setImageResource(R.drawable.main_msg_in);
                ((TextView)findViewById(R.id.txt_chat)).setTextColor(ContextCompat.getColor(this, R.color.color_cheng));
                ((ImageView)findViewById(R.id.img_friend)).setImageResource(R.drawable.main_friend_out);
                ((TextView)findViewById(R.id.txt_link)).setTextColor(ContextCompat.getColor(this, R.color.color_blue));
                ((ImageView)findViewById(R.id.img_leaf)).setImageResource(R.drawable.main_leaf_out);
                ((TextView)findViewById(R.id.txt_leaf)).setTextColor(ContextCompat.getColor(this, R.color.color_blue));
                findViewById(R.id.btn_new_friend).setVisibility(View.GONE);
                findViewById(R.id.btn_team_request).setVisibility(View.GONE);
                break;
            case 1:
                fragmentTransaction
                        .hide(msgFrag)
                        .show(friendFrag)
                        .hide(teamFrag)
                        .hide(leafFrag)
                        .commit();
                txtTitle.setText("联系人");
                ((ImageView)findViewById(R.id.img_chat)).setImageResource(R.drawable.main_msg_out);
                ((TextView)findViewById(R.id.txt_chat)).setTextColor(ContextCompat.getColor(this, R.color.color_blue));
                ((ImageView)findViewById(R.id.img_friend)).setImageResource(R.drawable.main_friend_in);
                ((TextView)findViewById(R.id.txt_link)).setTextColor(ContextCompat.getColor(this, R.color.color_cheng));
                ((ImageView)findViewById(R.id.img_leaf)).setImageResource(R.drawable.main_leaf_out);
                ((TextView)findViewById(R.id.txt_leaf)).setTextColor(ContextCompat.getColor(this, R.color.color_blue));
                findViewById(R.id.btn_new_friend).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_team_request).setVisibility(View.GONE);
                break;
            case 2:
                fragmentTransaction
                        .hide(msgFrag)
                        .hide(friendFrag)
                        .show(teamFrag)
                        .hide(leafFrag)
                        .commit();
                txtTitle.setText("团队");
                ((ImageView)findViewById(R.id.img_chat)).setImageResource(R.drawable.main_msg_out);
                ((TextView)findViewById(R.id.txt_chat)).setTextColor(ContextCompat.getColor(this, R.color.color_blue));
                ((ImageView)findViewById(R.id.img_friend)).setImageResource(R.drawable.main_friend_out);
                ((TextView)findViewById(R.id.txt_link)).setTextColor(ContextCompat.getColor(this, R.color.color_blue));
                ((ImageView)findViewById(R.id.img_leaf)).setImageResource(R.drawable.main_leaf_out);
                ((TextView)findViewById(R.id.txt_leaf)).setTextColor(ContextCompat.getColor(this, R.color.color_blue));
                findViewById(R.id.btn_new_friend).setVisibility(View.GONE);
                findViewById(R.id.btn_team_request).setVisibility(View.VISIBLE);
                break;
            case 3:
                fragmentTransaction
                        .hide(msgFrag)
                        .hide(friendFrag)
                        .hide(teamFrag)
                        .show(leafFrag)
                        .commit();
                txtTitle.setText("生活");
                ((ImageView)findViewById(R.id.img_chat)).setImageResource(R.drawable.main_msg_out);
                ((TextView)findViewById(R.id.txt_chat)).setTextColor(ContextCompat.getColor(this, R.color.color_blue));
                ((ImageView)findViewById(R.id.img_friend)).setImageResource(R.drawable.main_friend_out);
                ((TextView)findViewById(R.id.txt_link)).setTextColor(ContextCompat.getColor(this, R.color.color_blue));
                ((ImageView)findViewById(R.id.img_leaf)).setImageResource(R.drawable.main_leaf_in);
                ((TextView)findViewById(R.id.txt_leaf)).setTextColor(ContextCompat.getColor(this, R.color.color_cheng));
                findViewById(R.id.btn_new_friend).setVisibility(View.GONE);
                findViewById(R.id.btn_team_request).setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }





    @Override
    protected void onDestroy() {

        /**
         * 移除绑定
         * 1.移除请求好友列表绑定
         * 2.移除消息绑定
         */
        RequestModel.getInstance().listeners.remove(modelListener);
        MsgModel.getInstance().listeners.remove(modelListener);
        //关闭电源模式
        wl.release();

        super.onDestroy();
    }
}
