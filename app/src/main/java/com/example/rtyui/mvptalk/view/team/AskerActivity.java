package com.example.rtyui.mvptalk.view.team;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.adapter.TeamAskerAdapter;
import com.example.rtyui.mvptalk.back.Msger;
import com.example.rtyui.mvptalk.model.TeamRequestModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;

/**
 * Created by rtyui on 2018/5/15.
 */

public class AskerActivity extends Activity {

    private ViewStub loading = null;
    private ListView lst = null;

    private TeamAskerAdapter adapter = null;

    private OnModelChangeListener modelListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_asker);

        loading = findViewById(R.id.loading);

        lst = findViewById(R.id.lst);


        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lst.setAdapter(adapter = new TeamAskerAdapter(this, new TeamAskerAdapter.OnActionListener() {
            @Override
            public void doAgree(final int position) {
                final int id = TeamRequestModel.getInstance().askerBeans.get(position).uid;
                new NetTaskCode(new NetTaskCodeListener() {
                    @Override
                    public void before() {
                        loading.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public int middle() {
                        return TeamRequestModel.getInstance().agree(position);
                    }

                    @Override
                    public void after(int code) {
                        loading.setVisibility(View.GONE);
                        if (code == App.NET_SUCCEED){
                            Toast.makeText(AskerActivity.this, "同意申请成功", Toast.LENGTH_SHORT).show();
                            TeamRequestModel.getInstance().actListeners();
                            App.sendBroadCast(App.SEND_ACTION, new Msger(System.currentTimeMillis(), App.C2S_AGREE_TEAM, id + "").toString());
                        }else{
                            Toast.makeText(AskerActivity.this, "同意申请失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).execute();
            }

            @Override
            public void doDisAgree(final int position) {
                new NetTaskCode(new NetTaskCodeListener() {
                    @Override
                    public void before() {
                        loading.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public int middle() {
                        return TeamRequestModel.getInstance().refuse(position);
                    }

                    @Override
                    public void after(int code) {
                        loading.setVisibility(View.GONE);
                        if (code == App.NET_SUCCEED){
                            Toast.makeText(AskerActivity.this, "拒绝申请成功", Toast.LENGTH_SHORT).show();
                            TeamRequestModel.getInstance().actListeners();
                        }else{
                            Toast.makeText(AskerActivity.this, "拒绝申请失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).execute();
            }
        }));

        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                adapter.notifyDataSetChanged();
            }
        };
        TeamRequestModel.getInstance().listeners.add(modelListener);

        new NetTaskCode(new NetTaskCodeListener() {
            @Override
            public void before() {
                loading.setVisibility(View.VISIBLE);
            }

            @Override
            public int middle() {
                return TeamRequestModel.getInstance().loadRequest();
            }

            @Override
            public void after(int code) {
                loading.setVisibility(View.GONE);
                if (code == App.NET_SUCCEED){
                    TeamRequestModel.getInstance().actListeners();
                }
            }
        }).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        TeamRequestModel.getInstance().listeners.remove(modelListener);
    }
}
