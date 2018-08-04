package com.example.rtyui.mvptalk.view.msg;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.adapter.MsgAdapter;
import com.example.rtyui.mvptalk.adapter.TalkAdapter;
import com.example.rtyui.mvptalk.back.Msger;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.permission.OnAskAppearListener;
import com.example.rtyui.mvptalk.tool.permission.PermissionAsker;
import com.example.rtyui.mvptalk.view.common.FileChooseActivity;
import com.example.rtyui.mvptalk.view.main.MainActivity;
import com.example.rtyui.mvptalk.view.mine.ChooseAlbumActivity;
import com.example.rtyui.mvptalk.view.user.UserIndexActivity;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
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

    private PermissionAsker albumAsker;

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

        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                txtToName.setText(FriendModel.getInstance().getUserById(userId).remark);
                talkAdapter.notifyDataSetChanged();
            }
        };

        MsgModel.getInstance().listeners.add(modelListener);
        FriendModel.getInstance().listeners.add(modelListener);
        FriendModel.getInstance().CURRENT_TALK = userId;
    }

    private void initLayout(){
        userId = getIntent().getIntExtra("userId", -1);

        edtMsg = findViewById(R.id.edt_msg);
        listView = findViewById(R.id.lst);
        txtToName = findViewById(R.id.txt_toname);
        txtToName.setText(FriendModel.getInstance().getUserById(userId).remark);

        albumAsker = new PermissionAsker(this, new OnAskAppearListener() {
            @Override
            public void onAppear() {
                Intent intent = new Intent(TalkActivity.this, ChooseAlbumActivity.class);
                intent.putExtra("sign", App.PHOTO_CHOOSE_SIGN_SENDIMG);
                startActivity(intent);
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, 2, 2, "为了发送图片，请允许我们使用读取文件权限", true);

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (edtMsg.getText().toString().trim().equals("")){
            }else {
                long time = System.currentTimeMillis();
                ChatBean chatBean = new ChatBean(userId, AccountModel.getInstance().currentUser.id, App.MSG_CHAT + edtMsg.getText().toString(), time, App.CATEGORY_FRIEND);
                MsgModel.getInstance().add(chatBean);
                edtMsg.setText("");
                MsgModel.getInstance().actListeners();
                App.sendBroadCast(App.SEND_ACTION, new Msger(time, App.C2S_FRIEND_CHAT, new Gson().toJson(chatBean)).toString());
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

        findViewById(R.id.btn_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumAsker.onAsk();
            }
        });

        MsgModel.getInstance().setFriendReadById(userId);
        listView.setAdapter(talkAdapter = new TalkAdapter(this, userId));
        listView.setSelection(talkAdapter.getCount() - 1);
        MsgModel.getInstance().actListeners();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int sign = intent.getIntExtra("choose_sign", -1);
        switch (sign){
            case App.CHOOSE_IMG_INTENT:
                String temp = intent.getStringExtra("path");
                if (temp != null) {
                    final ChatBean chatBean = new ChatBean(userId, AccountModel.getInstance().currentUser.id, App.MSG_IMG + "file://" + temp, System.currentTimeMillis(), App.CATEGORY_FRIEND);
                    MsgModel.getInstance().add(chatBean);
                    MsgModel.getInstance().actListeners();
                    try {
                        final AVFile file = AVFile.withAbsoluteLocalPath("LeanCloud.png", temp);
                        file.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    App.sendBroadCast(App.SEND_ACTION, new Msger(chatBean.time, App.C2S_FRIEND_CHAT, new Gson().toJson(new ChatBean(userId, AccountModel.getInstance().currentUser.id, App.MSG_IMG + file.getUrl(), chatBean.time, App.CATEGORY_FRIEND))).toString());
                                }
                                else {
                                    MsgModel.getInstance().changeStatu(chatBean.time, App.MSG_SEND_BAD);
                                    MsgModel.getInstance().actListeners();
                                }
                            }
                        }, new ProgressCallback() {
                            @Override
                            public void done(Integer integer) {

                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        MsgModel.getInstance().changeStatu(chatBean.time, App.MSG_SEND_BAD);
                        MsgModel.getInstance().actListeners();
                    }
                }
                break;
            case App.CHOOSE_FILE_INTENT:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        albumAsker.onChoose(requestCode, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FriendModel.getInstance().CURRENT_TALK = -1;
        FriendModel.getInstance().listeners.remove(modelListener);
        MsgModel.getInstance().listeners.remove(modelListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        albumAsker.onSet(requestCode);
    }
}
