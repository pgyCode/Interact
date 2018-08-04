package com.example.rtyui.mvptalk.view.msg;

import android.Manifest;
import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.adapter.TeamTalkAdapter;
import com.example.rtyui.mvptalk.back.Msger;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.bean.TeamChatBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.permission.OnAskAppearListener;
import com.example.rtyui.mvptalk.tool.permission.PermissionAsker;
import com.example.rtyui.mvptalk.view.common.FileChooseActivity;
import com.example.rtyui.mvptalk.view.main.MainActivity;
import com.example.rtyui.mvptalk.view.mine.ChooseAlbumActivity;
import com.example.rtyui.mvptalk.view.team.TeamIndexActivity;
import com.google.gson.Gson;

import java.io.FileNotFoundException;

/**
 * Created by rtyui on 2018/4/2.
 */

public class TeamTalkActivity extends Activity{

    private EditText edtMsg = null;
    private ListView listView = null;
    private TeamTalkAdapter talkAdapter = null;

    private int id = -1;

    private TextView name = null;

    private OnModelChangeListener modelListener = null;

    private PermissionAsker albumAsker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_talk);
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
                name.setText(TeamModel.getInstance().OUTER_getTeamById(id).nickname);
                talkAdapter.notifyDataSetChanged();
            }
        };

        MsgModel.getInstance().listeners.add(modelListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TeamTalkActivity.this, TeamIndexActivity.class);
                intent.putExtra("id", TeamModel.getInstance().teamBeans.get(position - listView.getHeaderViewsCount()).id);
                startActivity(intent);
            }
        });
        TeamModel.getInstance().CURRENT_TALK = id;
    }


    private void initLayout(){
        id = getIntent().getIntExtra("id", -1);

        edtMsg = findViewById(R.id.edt_msg);
        listView = findViewById(R.id.lst);
        name = findViewById(R.id.txt_toname);
        name.setText(TeamModel.getInstance().OUTER_getTeamById(id).nickname);

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (edtMsg.getText().toString().trim().equals("")){
            }else {
                long time = System.currentTimeMillis();
                TeamChatBean chatBean = new TeamChatBean(id, AccountModel.getInstance().currentUser.id, App.MSG_CHAT + edtMsg.getText().toString(), time, App.CATEGORY_TEAM, TeamModel.getInstance().OUTER_getTeamById(id).remark, AccountModel.getInstance().currentUser.nickname, AccountModel.getInstance().currentUser.headImgUrl);
                MsgModel.getInstance().add(new ChatBean(chatBean.recvId, chatBean.sendId, chatBean.msg, chatBean.time, chatBean.category));
                edtMsg.setText("");
                MsgModel.getInstance().actListeners();
                App.sendBroadCast(App.SEND_ACTION, new Msger(time, App.C2S_TEAM_CHAT, new Gson().toJson(chatBean)).toString());
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
                Intent intent = new Intent(TeamTalkActivity.this, TeamIndexActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        albumAsker = new PermissionAsker(this, new OnAskAppearListener() {
            @Override
            public void onAppear() {
                Intent intent = new Intent(TeamTalkActivity.this, ChooseAlbumActivity.class);
                intent.putExtra("sign", App.PHOTO_CHOOSE_SIGN_TEAM_SENDIMG);
                startActivity(intent);
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, 2, 2, "为了能够读取图片，请允许我们使用读取文件权限", true);
        findViewById(R.id.btn_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumAsker.onAsk();
            }
        });

//        findViewById(R.id.btn_file).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (ActivityCompat.checkSelfPermission(TeamTalkActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        TeamTalkActivity.this.requestPermissions(
//                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
//                    }else {
//                        Toast.makeText(TeamTalkActivity.this, "权限已申请", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(TeamTalkActivity.this, FileChooseActivity.class);
//                        intent.putExtra("sign", App.PHOTO_CHOOSE_SIGN_TEAM_SENDIMG);
//                        startActivity(intent);
//                    }
//                }
//                else{
//                    Intent intent = new Intent(TeamTalkActivity.this, FileChooseActivity.class);
//                    intent.putExtra("sign", App.PHOTO_CHOOSE_SIGN_TEAM_SENDIMG);
//                    startActivity(intent);
//                }
//            }
//        });

        MsgModel.getInstance().setTeamReadById(id);
        listView.setAdapter(talkAdapter = new TeamTalkAdapter(this, id));
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
                    final TeamChatBean chatBean = new TeamChatBean(id, AccountModel.getInstance().currentUser.id, App.MSG_IMG + "file://" + temp, System.currentTimeMillis(), App.CATEGORY_TEAM, TeamModel.getInstance().OUTER_getTeamById(id).remark, AccountModel.getInstance().currentUser.nickname, AccountModel.getInstance().currentUser.headImgUrl);
                    MsgModel.getInstance().add(new ChatBean(chatBean.recvId, chatBean.sendId, chatBean.msg, chatBean.time, chatBean.category));
                    MsgModel.getInstance().actListeners();
                    try {
                        final AVFile file = AVFile.withAbsoluteLocalPath("LeanCloud.png", temp);
                        file.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    chatBean.msg = App.MSG_IMG + file.getUrl();
                                    App.sendBroadCast(App.SEND_ACTION, new Msger(chatBean.time, App.C2S_TEAM_CHAT, new Gson().toJson(chatBean)).toString());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        albumAsker.onSet(requestCode);
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
        TeamModel.getInstance().CURRENT_TALK = -1;
    }
}
