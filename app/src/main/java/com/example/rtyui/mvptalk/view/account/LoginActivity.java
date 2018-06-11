package com.example.rtyui.mvptalk.view.account;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;
import com.example.rtyui.mvptalk.view.main.MainActivity;

import static com.example.rtyui.mvptalk.tool.App.NET_FAil;
import static com.example.rtyui.mvptalk.tool.App.NET_SUCCEED;

public class LoginActivity extends Activity implements View.OnClickListener {

    private ViewStub loading = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_login);

        loading = findViewById(R.id.loading);

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_find).setOnClickListener(this);
        findViewById(R.id.btn_regist).setOnClickListener(this);

        if (AccountModel.getInstance().currentUser != null){
            goMain();
        }
    }

    public void beforeLogin() {
        loading.setVisibility(View.VISIBLE);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow((findViewById(R.id.edt_password)).getWindowToken(), 0);
    }

    public void afterLogin(int code) {
        loading.setVisibility(View.GONE);
        switch (code){
            case NET_FAil:
                Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();
                break;
            case NET_SUCCEED:
                goMain();
                break;
        }
    }

    private void goMain(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_find:
                Toast.makeText(this, "暂时不提供找回密码功能", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_regist:
                startActivity(new Intent(this, RegistActivity.class));
                finish();
                break;
            case R.id.btn_login:
                new NetTaskCode(new NetTaskCodeListener() {
                    @Override
                    public void before() {
                        beforeLogin();
                    }

                    @Override
                    public int middle() {
                        return AccountModel.getInstance().doLogin(((EditText)(LoginActivity.this.findViewById(R.id.edt_username))).getText().toString().trim(),
                                ((EditText)(LoginActivity.this.findViewById(R.id.edt_password))).getText().toString().trim());
                    }

                    @Override
                    public void after(int code) {
                        afterLogin(code);
                    }
                }).execute();
                break;
        }
    }
}
