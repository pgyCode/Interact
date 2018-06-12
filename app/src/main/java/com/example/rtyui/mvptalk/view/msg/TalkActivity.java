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
import com.avos.avoscloud.SaveCallback;
import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.adapter.TalkAdapter;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
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
        txtToName.setText(FriendModel.getInstance().getUserById(userId).nickname);

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtMsg.getText().toString().trim().equals("")){
                }else {
                    doSend(App.MSG_CHAT + edtMsg.getText().toString());
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(TalkActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        TalkActivity.this.requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                    }else {
                        Toast.makeText(TalkActivity.this, "权限已申请", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(TalkActivity.this, ChooseAlbumActivity.class);
                        intent.putExtra("sign", App.PHOTO_CHOOSE_SIGN_SENDIMG);
                        startActivity(intent);
                    }
                }
                else{
                    Intent intent = new Intent(TalkActivity.this, ChooseAlbumActivity.class);
                    intent.putExtra("sign", App.PHOTO_CHOOSE_SIGN_SENDIMG);
                    startActivity(intent);
                }
            }
        });

        try{MsgModel.getInstance().getCombeanById(userId).unread = 0;}catch(Exception e){};
        listView.setAdapter(talkAdapter = new TalkAdapter(this, userId));
        MsgModel.getInstance().actListeners();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String temp = intent.getStringExtra("path");
        if (temp != null) {
            try {
                final AVFile file = AVFile.withAbsoluteLocalPath("LeanCloud.png", temp);
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        doSend(App.MSG_IMG + file.getUrl());
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void doSend(String msg) {
        ChatBean chatBean = new ChatBean(userId, AccountModel.getInstance().currentUser.id, msg, AccountModel.getInstance().currentUser.nickname, AccountModel.getInstance().currentUser.headImgUrl, FriendModel.getInstance().getUserById(userId).nickname, FriendModel.getInstance().getUserById(userId).headImgUrl, System.currentTimeMillis());
        Intent intent = new Intent(App.SEND_CHAT_ACTION);
        intent.putExtra("data", new Gson().toJson(chatBean));
        intent.putExtra("id", userId);
        intent.putExtra("position", MsgModel.getInstance().addSend(chatBean));
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(TalkActivity.this);
        localBroadcastManager.sendBroadcast(intent);
        edtMsg.setText("");
        talkAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "权限已申请", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TalkActivity.this, ChooseAlbumActivity.class);
                    intent.putExtra("sign", App.PHOTO_CHOOSE_SIGN_SENDIMG);
                    startActivity(intent);
                }else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("权限申请").setMessage("为了能够设设置头像，请允许我们使用读取文件权限")
                            .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goToAppSetting();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    dialog.show();
                }
            }
        }
    }


    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 123);
        return;
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
