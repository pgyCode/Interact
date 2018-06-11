package com.example.rtyui.mvptalk.view.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;

import static com.example.rtyui.mvptalk.tool.App.NET_FAil;
import static com.example.rtyui.mvptalk.tool.App.NET_SUCCEED;

/**
 * Created by rtyui on 2018/5/5.
 */

public class RegistActivity extends Activity{

    private ViewStub loading = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_regist);
        initLayout();
    }

    private void initLayout(){
        findViewById(R.id.btn_regist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new NetTaskCode(new NetTaskCodeListener() {
                    public void before() {
                        beforeRegist();
                    }

                    public int middle() {
                        return AccountModel.getInstance().doRegist(
                                ((EditText) findViewById(R.id.edt_nickname)).getText().toString(),
                                ((EditText) findViewById(R.id.edt_password)).getText().toString()
                        );
                    }

                    public void after(int code) {
                        afterRegist(code);
                    }
                }).execute();
            }
        });
        loading = findViewById(R.id.loading);
    }

    public void afterRegist(int code) {
        loading.setVisibility(View.GONE);
        switch (code){
            case NET_FAil:
                Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT).show();
                break;
            case NET_SUCCEED:
                startActivity(new Intent(this, WelcomeActivity.class));
                finish();
                break;
        }
    }

    public void beforeRegist() {
        loading.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}