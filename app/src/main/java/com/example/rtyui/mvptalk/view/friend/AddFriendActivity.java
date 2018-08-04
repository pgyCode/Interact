package com.example.rtyui.mvptalk.view.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.back.Msger;
import com.example.rtyui.mvptalk.bean.ActionBean;
import com.example.rtyui.mvptalk.bean.AddFriendBean;
import com.example.rtyui.mvptalk.bean.InfoActionBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;
import com.example.rtyui.mvptalk.view.main.MainActivity;
import com.google.gson.Gson;

/**
 * Created by rtyui on 2018/5/5.
 */

public class AddFriendActivity extends Activity{

    private final int NET_FAil = -1;
    private final int LOGIN_SUCCEED = 2;

    private ViewStub loading = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_add_friend);

        loading = findViewById(R.id.loading);


        //返回
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String temp = ((EditText)findViewById(R.id.edt_id)).getText().toString().trim();
                if (temp.equals("")){
                    return;
                }
                new NetTaskCode(new NetTaskCodeListener() {
                    @Override
                    public void before() {
                        startActivity(new Intent(AddFriendActivity.this, MainActivity.class));
                    }

                    @Override
                    public int middle() {
                        return RequestModel.getInstance().addFriend(Integer.valueOf(temp));
                    }

                    @Override
                    public void after(int code) {
                        if (code == App.NET_SUCCEED){
                            Toast.makeText(AddFriendActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                            long time = System.currentTimeMillis();
                            App.sendBroadCast(App.SEND_ACTION,
                                    new Msger(time, App.C2S_ADD_FRIEND,
                                            new Gson().toJson(
                                                    new InfoActionBean(AccountModel.getInstance().currentUser.id,
                                                                        AccountModel.getInstance().currentUser.nickname,
                                                                        AccountModel.getInstance().currentUser.headImgUrl, Integer.parseInt(temp), time))).toString());
                        }else
                            Toast.makeText(AddFriendActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    }
                }).execute();
            }
        });
    }
}
