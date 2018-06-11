package com.example.rtyui.mvptalk.view.mine;

import android.accounts.Account;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;

public class ChangeNickActivity extends Activity {


    private EditText edt_nick;
    private ViewStub loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_change_nick);

        edt_nick = findViewById(R.id.edt_nick);
        loading = findViewById(R.id.loading);

        findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NetTaskCode(new NetTaskCodeListener() {
                    @Override
                    public void before() {
                        loading.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public int middle() {
                        return AccountModel.getInstance().changeNick(edt_nick.getText().toString());
                    }

                    @Override
                    public void after(int code) {
                        loading.setVisibility(View.GONE);
                        if (code == App.NET_SUCCEED){
                            Toast.makeText(ChangeNickActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            AccountModel.getInstance().actListeners();
                            finish();
                        }
                        else
                            Toast.makeText(ChangeNickActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                    }
                }).execute();
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
