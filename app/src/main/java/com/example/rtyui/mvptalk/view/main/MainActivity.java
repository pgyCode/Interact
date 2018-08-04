package com.example.rtyui.mvptalk.view.main;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.back.MyService;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.model.TeamRequestModel;
import com.example.rtyui.mvptalk.model.TempUserModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.model.MySqliteHelper;
import com.example.rtyui.mvptalk.view.friend.FriendFragment;
import com.example.rtyui.mvptalk.view.msg.MsgFragment;
import com.example.rtyui.mvptalk.view.own.LeafFragment;
import com.example.rtyui.mvptalk.view.team.TeamFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rtyui on 2018/4/25.
 */

public class MainActivity extends FragmentActivity implements View.OnClickListener {


    private List<Fragment> fragments;

    private TextView txtTitle = null;

    private TextView txtWarn = null;
    private TextView txtWarnMore = null;

    private ViewPager viewPager;

    private final int SUM = 4;

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

        /**
         * 初始化本地数据
         * 1.好友信息
         * 2.好友聊天信息
         * 3.团队信息
         * 4.团队聊天信息
         */
        FriendModel.getInstance().init();
        TeamModel.getInstance().OUTER_init();
        TempUserModel.getInstance().init();
        MsgModel.getInstance().init();
        TeamModel.getInstance().actListeners();
        MsgModel.getInstance().actListeners();
        FriendModel.getInstance().actListeners();
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
        txtWarn = findViewById(R.id.txt_warn);
        txtWarnMore = findViewById(R.id.txt_warn_more);
        viewPager = findViewById(R.id.viewpager);

        fragments = new ArrayList<>();
        fragments.add(new MsgFragment());
        fragments.add(new FriendFragment());
        fragments.add(new TeamFragment());
        fragments.add(new LeafFragment());

        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return SUM;
            }
        });


        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                if (MsgModel.getInstance().getUnread() > 0) {
                    txtWarn.setVisibility(View.VISIBLE);
                }else {
                    txtWarn.setVisibility(View.GONE);
                }
                if (RequestModel.getInstance().isWarn() || TeamRequestModel.getInstance().isWarn()){
                    txtWarnMore.setVisibility(View.VISIBLE);
                }else{
                    txtWarnMore.setVisibility(View.GONE);
                }

                if (!AccountModel.getInstance().onLine) {
                    findViewById(R.id.sign_offline).setBackgroundResource(R.drawable.red_oval);
                    findViewById(R.id.sign_online).setBackgroundResource(R.drawable.grew_oval);
                }else{
                    findViewById(R.id.sign_online).setBackgroundResource(R.drawable.green_oval);
                    findViewById(R.id.sign_offline).setBackgroundResource(R.drawable.grew_oval);
                }
            }
        };
        RequestModel.getInstance().listeners.add(modelListener);
        TeamRequestModel.getInstance().listeners.add(modelListener);
        MsgModel.getInstance().listeners.add(modelListener);
        AccountModel.getInstance().listeners.add(modelListener);

        findViewById(R.id.btn_msg).setOnClickListener(this);
        findViewById(R.id.btn_friend).setOnClickListener(this);
        findViewById(R.id.btn_team).setOnClickListener(this);
        findViewById(R.id.btn_more).setOnClickListener(this);


        replaceFragment(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                replaceFragment(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_msg:
                viewPager.setCurrentItem(0);
                break;
            case R.id.btn_friend:
                viewPager.setCurrentItem(1);
                break;
            case R.id.btn_team:
                viewPager.setCurrentItem(2);
                break;
            case R.id.btn_more:
                viewPager.setCurrentItem(3);
                break;
        }
    }


    private void replaceFragment(int sign){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (sign){
            case 0:
                txtTitle.setText("消息");
                findViewById(R.id.label_msg).setBackgroundResource(R.color.T_main);
                ((TextView)findViewById(R.id.txt_msg)).setTextColor(ContextCompat.getColor(this, R.color.T_main));
                findViewById(R.id.label_friend).setBackgroundResource(R.color.T_touming);
                ((TextView)findViewById(R.id.txt_friend)).setTextColor(ContextCompat.getColor(this, R.color.T_black));
                findViewById(R.id.label_team).setBackgroundResource(R.color.T_touming);
                ((TextView)findViewById(R.id.txt_team)).setTextColor(ContextCompat.getColor(this, R.color.T_black));
                findViewById(R.id.label_more).setBackgroundResource(R.color.T_touming);
                ((TextView)findViewById(R.id.txt_more)).setTextColor(ContextCompat.getColor(this, R.color.T_black));
                break;
            case 1:
                txtTitle.setText("联系人");
                findViewById(R.id.label_msg).setBackgroundResource(R.color.T_touming);
                ((TextView)findViewById(R.id.txt_msg)).setTextColor(ContextCompat.getColor(this, R.color.T_black));
                findViewById(R.id.label_friend).setBackgroundResource(R.color.T_main);
                ((TextView)findViewById(R.id.txt_friend)).setTextColor(ContextCompat.getColor(this, R.color.T_main));
                findViewById(R.id.label_team).setBackgroundResource(R.color.T_touming);
                ((TextView)findViewById(R.id.txt_team)).setTextColor(ContextCompat.getColor(this, R.color.T_black));
                findViewById(R.id.label_more).setBackgroundResource(R.color.T_touming);
                ((TextView)findViewById(R.id.txt_more)).setTextColor(ContextCompat.getColor(this, R.color.T_black));
                break;
            case 2:
                txtTitle.setText("团队");
                findViewById(R.id.label_msg).setBackgroundResource(R.color.T_touming);
                ((TextView)findViewById(R.id.txt_msg)).setTextColor(ContextCompat.getColor(this, R.color.T_black));
                findViewById(R.id.label_friend).setBackgroundResource(R.color.T_touming);
                ((TextView)findViewById(R.id.txt_friend)).setTextColor(ContextCompat.getColor(this, R.color.T_black));
                findViewById(R.id.label_team).setBackgroundResource(R.color.T_main);
                ((TextView)findViewById(R.id.txt_team)).setTextColor(ContextCompat.getColor(this, R.color.T_main));
                findViewById(R.id.label_more).setBackgroundResource(R.color.T_touming);
                ((TextView)findViewById(R.id.txt_more)).setTextColor(ContextCompat.getColor(this, R.color.T_black));
                break;
            case 3:
                txtTitle.setText("more");
                findViewById(R.id.label_msg).setBackgroundResource(R.color.T_touming);
                ((TextView)findViewById(R.id.txt_msg)).setTextColor(ContextCompat.getColor(this, R.color.T_black));
                findViewById(R.id.label_friend).setBackgroundResource(R.color.T_touming);
                ((TextView)findViewById(R.id.txt_friend)).setTextColor(ContextCompat.getColor(this, R.color.T_black));
                findViewById(R.id.label_team).setBackgroundResource(R.color.T_touming);
                ((TextView)findViewById(R.id.txt_team)).setTextColor(ContextCompat.getColor(this, R.color.T_black));
                findViewById(R.id.label_more).setBackgroundResource(R.color.T_main);
                ((TextView)findViewById(R.id.txt_more)).setTextColor(ContextCompat.getColor(this, R.color.T_main));
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
        TeamRequestModel.getInstance().listeners.remove(modelListener);
        MsgModel.getInstance().listeners.remove(modelListener);
        AccountModel.getInstance().listeners.remove(modelListener);
        //关闭电源模式
        wl.release();

        super.onDestroy();
    }
}
