package com.example.rtyui.mvptalk.view.user;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.view.main.MainActivity;
import com.example.rtyui.mvptalk.view.msg.TalkActivity;

import static com.example.rtyui.mvptalk.tool.App.NET_FAil;
import static com.example.rtyui.mvptalk.tool.App.NET_SUCCEED;

/**
 * Created by rtyui on 2018/5/31.
 */

public class UserIndexChangeRemarkActivity extends Activity {

    private int id;//用户id
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_change_remark);

        id = getIntent().getIntExtra("id", -1);

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
                        startActivity(new Intent(UserIndexChangeRemarkActivity.this, TalkActivity.class));
                    }

                    @Override
                    protected Integer doInBackground(Void... voids) {
                        return FriendModel.getInstance().changeRemark(((EditText)(findViewById(R.id.edt_remark))).getText().toString(), id);
                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        if (integer == App.NET_SUCCEED) {
                            FriendModel.getInstance().actListeners();
                            Toast.makeText(UserIndexChangeRemarkActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(UserIndexChangeRemarkActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        });
    }
}
