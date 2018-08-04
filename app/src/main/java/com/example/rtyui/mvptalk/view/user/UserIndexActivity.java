package com.example.rtyui.mvptalk.view.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.back.Msger;
import com.example.rtyui.mvptalk.bean.ActionBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MyImgShow;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;
import com.example.rtyui.mvptalk.tool.dialoger.AlertDialoger;
import com.example.rtyui.mvptalk.tool.dialoger.OnDialogChooseListener;
import com.example.rtyui.mvptalk.view.main.MainActivity;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;

/**
 * Created by rtyui on 2018/5/31.
 */

public class UserIndexActivity extends Activity {

    private int id;//用户id

    private TextView txtRemark = null;
    private TextView txtId = null;

    private OnModelChangeListener modelListener = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_index);

        txtId = findViewById(R.id.txt_id);
        txtRemark = findViewById(R.id.txt_remark);

        id = getIntent().getIntExtra("id", -1);

        MyImgShow.showNetImgCircle(this, FriendModel.getInstance().getUserById(id).headImgUrl, (ImageView)findViewById(R.id.img_head));
        ((TextView)findViewById(R.id.txt_nick)).setText(FriendModel.getInstance().getUserById(id).nickname);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtRemark.setText("好友备注：" + FriendModel.getInstance().getUserById(id).remark);
        txtId.setText("好友账号：" + id);

        findViewById(R.id.btn_remark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserIndexActivity.this, UserIndexChangeRemarkActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialoger(UserIndexActivity.this,
                        "好友删除",
                        "你确定要删除这位好友吗？",
                        new OnDialogChooseListener(){

                            @Override
                            public void onPositive() {
                                long time = System.currentTimeMillis();
                                App.sendBroadCast(App.SEND_ACTION, new Msger(time, App.C2S_DEL_FRIEND, new Gson().toJson(new ActionBean(AccountModel.getInstance().currentUser.id, id, time))).toString());
                                startActivity(new Intent(UserIndexActivity.this, MainActivity.class));
                            }

                            @Override
                            public void onNegative() {

                            }
                        }).create();
                alertDialog.show();
            }
        });


        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                txtRemark.setText("好友备注：" + FriendModel.getInstance().getUserById(id).remark);
                txtId.setText("好友账号：" + id);
            }
        };

        FriendModel.getInstance().listeners.add(modelListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
