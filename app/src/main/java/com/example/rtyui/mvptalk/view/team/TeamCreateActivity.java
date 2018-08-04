package com.example.rtyui.mvptalk.view.team;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;
import com.example.rtyui.mvptalk.view.main.MainActivity;

public class TeamCreateActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_create);



        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String temp = ((EditText)findViewById(R.id.edt_input)).getText().toString().trim();
                if (temp.equals("")){
                    return;
                }

                new NetTaskCode(new NetTaskCodeListener() {
                    @Override
                    public void before() {
                        startActivity(new Intent(TeamCreateActivity.this, MainActivity.class));
                    }

                    @Override
                    public int middle() {
                        return TeamModel.getInstance().NET_createTeam(temp);
                    }

                    @Override
                    public void after(int code) {
                        if (code == App.NET_SUCCEED){
                            Toast.makeText(TeamCreateActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                            TeamModel.getInstance().actListeners();
                        }else
                            Toast.makeText(TeamCreateActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                    }
                }).execute();
            }
        });
    }
}
