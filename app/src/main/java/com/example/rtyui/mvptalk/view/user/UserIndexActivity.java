package com.example.rtyui.mvptalk.view.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.MyImgShow;

import java.lang.ref.WeakReference;

/**
 * Created by rtyui on 2018/5/31.
 */

public class UserIndexActivity extends Activity {

    private int id;//用户id


    private OnModelChangeListener modelListener = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_index);

        id = getIntent().getIntExtra("id", -1);

        MyImgShow.showNetImgCircle(this, FriendModel.getInstance().getUserById(id).headImgUrl, (ImageView)findViewById(R.id.img_head));
        ((TextView)findViewById(R.id.txt_nick)).setText(FriendModel.getInstance().getUserById(id).nickname);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((TextView)findViewById(R.id.txt_remark)).setText(FriendModel.getInstance().getUserById(id).remark);

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
                Intent intent = new Intent(UserIndexActivity.this, UserIndexDelFriendActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });


        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                ((TextView)findViewById(R.id.txt_remark)).setText(FriendModel.getInstance().getUserById(id).remark);
            }
        };

        FriendModel.getInstance().listeners.add(modelListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
