package com.example.rtyui.mvptalk.view.team;

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
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MyImgShow;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;
import com.example.rtyui.mvptalk.tool.dialoger.AlertDialoger;
import com.example.rtyui.mvptalk.tool.dialoger.OnDialogChooseListener;
import com.example.rtyui.mvptalk.view.main.MainActivity;
import com.example.rtyui.mvptalk.view.user.UserIndexChangeRemarkActivity;

/**
 * Created by rtyui on 2018/5/31.
 */

public class TeamIndexActivity extends Activity {

    private int id;//用户id

    private TextView txtName = null;
    private TextView txtId = null;

    private OnModelChangeListener modelListener = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_index);

        txtId = findViewById(R.id.txt_id);
        txtName = findViewById(R.id.txt_name);

        id = getIntent().getIntExtra("id", -1);

        MyImgShow.showNetImgCircle(this, TeamModel.getInstance().OUTER_getTeamById(id).nickname, (ImageView)findViewById(R.id.img_head));
        ((TextView)findViewById(R.id.txt_nick)).setText(TeamModel.getInstance().OUTER_getTeamById(id).nickname);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtName.setText("团队名称：" + TeamModel.getInstance().OUTER_getTeamById(id).nickname);
        txtId.setText("团队账号：" + id);

        findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialoger(TeamIndexActivity.this,
                        "退出团队",
                        "你确定要退出这个团队吗？",
                        new OnDialogChooseListener(){

                            @Override
                            public void onPositive() {
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
                txtName.setText("团队名称：" + TeamModel.getInstance().OUTER_getTeamById(id).nickname);
                txtId.setText("团队账号：" + id);
            }
        };

        FriendModel.getInstance().listeners.add(modelListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
