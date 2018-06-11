package com.example.rtyui.mvptalk.view.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.bean.AddFriendBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.newMsg.InfoMsg;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MyImgShow;
import com.example.rtyui.mvptalk.tool.NetTaskSet;
import com.example.rtyui.mvptalk.tool.NetTaskSetListener;
import com.google.gson.Gson;

import static com.example.rtyui.mvptalk.tool.App.NET_FAil;
import static com.example.rtyui.mvptalk.tool.App.NET_SUCCEED;

public class AddFriendPageActivity extends Activity {


    private int id;
    private ViewStub viewLoading = null;
    private View viewOne = null;
    private View viewNone = null;

    private TextView txtNick;
    private ImageView imgHead;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_add_friend_page);

        id = getIntent().getIntExtra("id", -1);

        viewLoading = findViewById(R.id.loading);
        viewNone = findViewById(R.id.view_none);
        viewOne = findViewById(R.id.view_one);

        txtNick = findViewById(R.id.txt_nick);
        imgHead = findViewById(R.id.img_head);

        new NetTaskSet(new NetTaskSetListener() {
            @Override
            public void before() {
                viewLoading.setVisibility(View.VISIBLE);
                viewOne.setVisibility(View.GONE);
                viewNone.setVisibility(View.GONE);
            }

            @Override
            public String middle() {
                return FriendModel.getInstance().getFriend(id);
            }

            @Override
            public void after(String set) {
                try{

                    viewLoading.setVisibility(View.GONE);

                    //临时添加 补救
                    set = set.replace("[", "").replace("]", "");
                    //临时添加 补救

                    InfoMsg msg = new Gson().fromJson(set, InfoMsg.class);
                    if (set == null || msg.code != App.NET_SUCCEED)
                    {
                        Toast.makeText(AddFriendPageActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        viewNone.setVisibility(View.VISIBLE);
                        viewOne.setVisibility(View.GONE);
                    }
                    else{
                        Toast.makeText(AddFriendPageActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
                        viewOne.setVisibility(View.VISIBLE);
                        viewNone.setVisibility(View.GONE);
                        txtNick.setText(msg.data.Nickname);
                        MyImgShow.showNetImgCircle(AddFriendPageActivity.this, msg.data.HeadImgUrl, imgHead);
                    }
                }catch (Exception e){

                }

            }
        }).execute();

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Integer>(){

                    @Override
                    protected Integer doInBackground(Void... voids) {
                        return RequestModel.getInstance().addFriend(id);
                    }

                    @Override
                    protected void onPostExecute(Integer code) {
                        super.onPostExecute(code);
                        afterAddfriend(code);
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        beforeAddFriend();
                    }
                }.execute();
            }
        });


    }

    public void beforeAddFriend() {
        viewLoading.setVisibility(View.VISIBLE);
    }

    public void afterAddfriend(int code) {
        viewLoading.setVisibility(View.GONE);
        switch (code){
            case NET_FAil:
                Toast.makeText(this, "发送好友请求失败", Toast.LENGTH_SHORT).show();
                break;
            case NET_SUCCEED:
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
                id);
        intent.putExtra("data", new Gson().toJson(addFriendBean));
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent);
    }
}
