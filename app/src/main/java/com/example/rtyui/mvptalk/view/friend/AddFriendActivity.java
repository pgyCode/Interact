package com.example.rtyui.mvptalk.view.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.bean.AddFriendBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.tool.App;
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

                Intent intent = new Intent(AddFriendActivity.this, AddFriendPageActivity.class);
                intent.putExtra("id", Integer.valueOf(temp));
                startActivity(intent);
            }
        });
    }

    public void beforeAddFriend() {
        loading.setVisibility(View.VISIBLE);
    }

    public void afterAddfriend(int code) {
        loading.setVisibility(View.GONE);
        switch (code){
            case NET_FAil:
                Toast.makeText(this, "发送好友请求失败", Toast.LENGTH_SHORT).show();
                break;
            case LOGIN_SUCCEED:
                Toast.makeText(this, "请求成功，请等待", Toast.LENGTH_SHORT).show();
                sendBroadCast();
                break;
        }
    }

    private void sendBroadCast(){
        Intent intent = new Intent(App.SEND_ADD_FRIEND_ACTION);

        AddFriendBean addFriendBean = new AddFriendBean(AccountModel.getInstance().currentUser.id,
                AccountModel.getInstance().currentUser.nickname,
                AccountModel.getInstance().currentUser.headImgUrl,
                Integer.parseInt(((EditText)findViewById(R.id.edt_id)).getText().toString().trim()));
        intent.putExtra("data", new Gson().toJson(addFriendBean));
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent);
    }
}
