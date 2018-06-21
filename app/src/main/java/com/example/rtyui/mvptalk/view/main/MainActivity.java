package com.example.rtyui.mvptalk.view.main;

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

    private ContinueRecvBroadcastReceiver continueRecvBroadcastReceiver;

    private OnModelChangeListener modelListener;

    private PowerManager.WakeLock wl;//状态锁

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MySqliteHelper.getInstance().mkTable(ChatBean.class);
        MySqliteHelper.getInstance().mkTable(TeamChatBean.class);

        continueRecvBroadcastReceiver = new ContinueRecvBroadcastReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(App.RECV_CHAT_ACTION);
        intentFilter.addAction(App.GET_ADD_FRIEND_REQUEST);
        intentFilter.addAction(App.STATU_CHAT_ACTION);
        intentFilter.addAction(App.LINK_FRIEND_RESPONSE_RECV_OK);
        intentFilter.addAction(App.RECV_TEAM_CHAT_ACTION);
        localBroadcastManager.registerReceiver(continueRecvBroadcastReceiver, intentFilter);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initLayout();
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "talk:socket");
        wl.acquire();

        FriendModel.getInstance().init();
        MsgModel.getInstance().init();
        TeamModel.getInstance().OUTER_init();
        TeamMsgModel.getInstance().init();
        TeamMsgModel.getInstance().actListeners();
        TeamModel.getInstance().actListeners();
        MsgModel.getInstance().actListeners();
        FriendModel.getInstance().actListeners();


        new NetTaskCode(new NetTaskCodeListener() {
            @Override
            public void before() { }

            @Override
            public int middle() {
                FriendModel.getInstance().flush();
                TeamModel.getInstance().NET_flushTeam();
                MsgModel.getInstance().doFlush();
                RequestModel.getInstance().loadRequest();
                return 0;
            }

            @Override
            public void after(int code) {
                MsgModel.getInstance().actListeners();
                FriendModel.getInstance().actListeners();
                RequestModel.getInstance().actListeners();
                TeamModel.getInstance().actListeners();
            }
        }).execute();
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
            case R.id.btn_exit:
                Intent intent1 = new Intent(this, MyService.class);
                stopService(intent1);
                Intent intent = new Intent(App.DESTORY_PIPE);
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
                localBroadcastManager.sendBroadcast(intent);
                SharedPreferences sharedPreferences = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                MyLocalObject.delObject("linkFriends" + "_" + AccountModel.getInstance().currentUser.id);
                MySqliteHelper.getInstance().clear(ChatBean.class);
                AccountModel.getInstance().currentUser = null;
                finish();
                break;
            case R.id.btn_new_friend:
                startActivity(new Intent(this, NewFriendActivity.class));
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
    }

    @Override
    protected void onDestroy() {
        RequestModel.getInstance().listeners.remove(modelListener);
        MsgModel.getInstance().listeners.remove(modelListener);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(continueRecvBroadcastReceiver);
        wl.release();
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
        super.onDestroy();
    }
}
