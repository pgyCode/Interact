package com.example.rtyui.mvptalk.view.user;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.FriendModel;

import static com.example.rtyui.mvptalk.tool.App.NET_FAil;
import static com.example.rtyui.mvptalk.tool.App.NET_SUCCEED;

/**
 * Created by rtyui on 2018/5/31.
 */

public class UserIndexChangeRemarkActivity extends Activity {

    private int id;//用户id

    private ViewStub loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_change_remark);

        id = getIntent().getIntExtra("id", -1);

        loading = findViewById(R.id.loading);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Integer>(){

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        beforeChangeRemark();
                    }

                    @Override
                    protected Integer doInBackground(Void... voids) {
                        return FriendModel.getInstance().changeRemark(((EditText)(findViewById(R.id.edt_remark))).getText().toString(), id);
                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        afterChangeRemark(integer);
                        FriendModel.getInstance().actListeners();
                    }
                }.execute();
            }
        });
    }


    public void beforeChangeRemark() {
        loading.setVisibility(View.VISIBLE);
    }

    public void afterChangeRemark(int code) {
        loading.setVisibility(View.GONE);
        switch (code){
            case NET_FAil:
                Toast.makeText(this, "更改失败", Toast.LENGTH_SHORT).show();
                break;
            case NET_SUCCEED:
                Toast.makeText(this, "更改成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }
}
