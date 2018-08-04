package com.example.rtyui.mvptalk.view.team;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.back.Msger;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.TeamRequestModel;
import com.example.rtyui.mvptalk.newBean.TeamAskerBean;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;
import com.example.rtyui.mvptalk.view.main.MainActivity;
import com.google.gson.Gson;

/**
 * Created by rtyui on 2018/5/5.
 */

public class AddTeamActivity extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_add_team);

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
                        startActivity(new Intent(AddTeamActivity.this, MainActivity.class));
                    }

                    @Override
                    public int middle() {
                        return TeamRequestModel.getInstance().addTeam(Integer.valueOf(temp));
                    }

                    @Override
                    public void after(int code) {
                        if (code == App.NET_SUCCEED){
                            Toast.makeText(AddTeamActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                            long time = System.currentTimeMillis();
                            App.sendBroadCast(App.SEND_ACTION, new Msger(time, App.C2S_ADD_TEAM, new Gson().toJson(new TeamAskerBean(AccountModel.getInstance().currentUser.id, Integer.valueOf(temp), AccountModel.getInstance().currentUser.headImgUrl,AccountModel.getInstance().currentUser.nickname, time))).toString());
                        }else
                            Toast.makeText(AddTeamActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    }
                }).execute();
            }
        });
    }
}
