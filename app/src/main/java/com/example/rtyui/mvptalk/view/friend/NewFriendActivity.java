package com.example.rtyui.mvptalk.view.friend;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.adapter.FriendAskerAdapter;
import com.example.rtyui.mvptalk.back.Msger;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;

/**
 * Created by rtyui on 2018/5/15.
 */

public class NewFriendActivity extends Activity {

    private ViewStub loading = null;
    private ListView lst = null;

    private FriendAskerAdapter adapter = null;

    private OnModelChangeListener modelListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_friend_newfriend);

        loading = findViewById(R.id.loading);

        lst = findViewById(R.id.lst);


        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lst.setAdapter(adapter = new FriendAskerAdapter(this, new FriendAskerAdapter.OnActionListener() {
            @Override
            public void doAgree(final int position) {
                final int id = RequestModel.getInstance().addFriendBeans.get(position).recvId;
                new NetTaskCode(new NetTaskCodeListener() {
                    @Override
                    public void before() {
                        loading.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public int middle() {
                        return RequestModel.getInstance().AgreeRequest(position);
                    }

                    @Override
                    public void after(int code) {
                        loading.setVisibility(View.GONE);
                        if (code == App.NET_SUCCEED){
                            Toast.makeText(NewFriendActivity.this, "确认同意成功", Toast.LENGTH_SHORT).show();
                            App.sendBroadCast(App.SEND_ACTION, new Msger(System.currentTimeMillis(), App.C2S_AGREE_FRIEND, id + "").toString());
                            RequestModel.getInstance().actListeners();
                        }else{
                            Toast.makeText(NewFriendActivity.this, "确认同意失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).execute();
            }

            @Override
            public void doDisAgree(final int position) {
                new AsyncTask<Void, Void, Integer>(){

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        loading.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected Integer doInBackground(Void... voids) {
                        return RequestModel.getInstance().DisAgreeRequest(position);
                    }

                    @Override
                    protected void onPostExecute(Integer aVoid) {
                        super.onPostExecute(aVoid);
                        loading.setVisibility(View.GONE);
                    }
                }.execute();
            }
        }));

        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                adapter.notifyDataSetChanged();
            }
        };
        RequestModel.getInstance().listeners.add(modelListener);

        new NetTaskCode(new NetTaskCodeListener() {
            @Override
            public void before() {
                loading.setVisibility(View.VISIBLE);
            }

            @Override
            public int middle() {
                return RequestModel.getInstance().loadRequest();
            }

            @Override
            public void after(int code) {
                loading.setVisibility(View.GONE);
                if (code == App.NET_SUCCEED){
                    RequestModel.getInstance().actListeners();
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
        RequestModel.getInstance().listeners.remove(modelListener);
    }
}
