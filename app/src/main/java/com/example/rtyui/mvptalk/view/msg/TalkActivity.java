package com.example.rtyui.mvptalk.view.msg;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.adapter.TalkAdapter;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.view.main.MainActivity;
import com.example.rtyui.mvptalk.view.user.UserIndexActivity;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;

/**
 * Created by rtyui on 2018/4/2.
 */

public class TalkActivity extends Activity{

    private EditText edtMsg = null;
    private ListView listView = null;
    private TalkAdapter talkAdapter = null;

    private int userId = -1;

    private TextView txtToName = null;

    private OnModelChangeListener modelListener = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.talk);
        initLayout();
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow((findViewById(R.id.edt_msg)).getWindowToken(), 0);
                return false;
            }
        });

        txtToName.setText(FriendModel.getInstance().getUserById(userId).nickname);

        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                txtToName.setText(FriendModel.getInstance().getUserById(userId).nickname);
                talkAdapter.notifyDataSetChanged();
            }
        };

        MsgModel.getInstance().listeners.add(modelListener);
        FriendModel.getInstance().listeners.add(modelListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.CURRENT_TALK = userId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.CURRENT_TALK = -1;
    }

    private void initLayout(){
        userId = getIntent().getIntExtra("userId", -1);

        edtMsg = findViewById(R.id.edt_msg);
        listView = findViewById(R.id.lst);
        txtToName = findViewById(R.id.txt_toname);
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtMsg.getText().toString().trim().equals("")){
                }else {
                    doSend();
                }
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TalkActivity.this, UserIndexActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }
        });

        try{MsgModel.getInstance().getCombeanById(userId).unread = 0;}catch(Exception e){};
        listView.setAdapter(talkAdapter = new TalkAdapter(this, userId));
        MsgModel.getInstance().actListeners();
    }

    public void start() {

    }

    public void doSend() {
        ChatBean chatBean = new ChatBean(userId, AccountModel.getInstance().currentUser.id, edtMsg.getText().toString(), AccountModel.getInstance().currentUser.nickname, AccountModel.getInstance().currentUser.headImgUrl, FriendModel.getInstance().getUserById(userId).nickname, FriendModel.getInstance().getUserById(userId).headImgUrl, System.currentTimeMillis());
        Intent intent = new Intent(App.SEND_CHAT_ACTION);
        intent.putExtra("data", new Gson().toJson(chatBean));
        intent.putExtra("id", userId);
        intent.putExtra("position", MsgModel.getInstance().addSend(chatBean));
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(TalkActivity.this);
        localBroadcastManager.sendBroadcast(intent);
        edtMsg.setText("");
        MsgModel.getInstance().actListeners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FriendModel.getInstance().listeners.remove(modelListener);
        MsgModel.getInstance().listeners.remove(modelListener);
    }
}
