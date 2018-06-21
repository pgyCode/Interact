package com.example.rtyui.mvptalk.view.team;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeEasy;
import com.example.rtyui.mvptalk.tool.NetTaskCodeEasyListener;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;

public class TeamAddActivity extends Activity {

    private ViewStub loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_add);

        loading = findViewById(R.id.loading);

        findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String temp = ((EditText)findViewById(R.id.edt_input)).getText().toString().trim();
                if (temp.equals("")){
                    return;
                }

                new NetTaskCodeEasy(new NetTaskCodeEasyListener() {
                    @Override
                    public void before() {
                        loading.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public int middle() {
                        return TeamModel.getInstance().NET_addTeam(Integer.valueOf(temp));
                    }

                    @Override
                    public void failed() {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(TeamAddActivity.this, "请检查你的网络", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void succeed() {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(TeamAddActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    }
                }).execute();
            }
        });
    }
}
