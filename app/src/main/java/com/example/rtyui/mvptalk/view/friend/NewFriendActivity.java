package com.example.rtyui.mvptalk.view.friend;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.adapter.FriendPageAdapter;
import com.example.rtyui.mvptalk.bean.AddFriendBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;

/**
 * Created by rtyui on 2018/5/15.
 */

public class NewFriendActivity extends Activity {

    private final int NET_FAil = -1;
    private final int LOGIN_SUCCEED = 2;

    private ViewStub loading = null;
    private ViewStub noany = null;
    private ListView lst = null;

    private FriendPageAdapter adapter = null;

    private OnModelChangeListener modelListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_friend_newfriend);

        loading = findViewById(R.id.loading);
        noany = findViewById(R.id.noany);

        lst = findViewById(R.id.lst);

        lst.setAdapter(adapter = new FriendPageAdapter(this, new FriendPageAdapter.OnActionListener() {
            @Override
            public void doAgree(final int position) {
                new AsyncTask<Void, Void, Integer>(){

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        beforeLoading();
                    }

                    @Override
                    protected Integer doInBackground(Void... voids) {
                        return RequestModel.getInstance().AgreeRequest(position);
                    }

                    @Override
                    protected void onPostExecute(Integer aVoid) {
                        super.onPostExecute(aVoid);
                        afterLoading(aVoid);
                        sendBroadCast(position);
                    }
                }.execute();
            }

            @Override
            public void doDisAgree(final int position) {
                new AsyncTask<Void, Void, Integer>(){

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        beforeLoading();
                    }

                    @Override
                    protected Integer doInBackground(Void... voids) {
                        return RequestModel.getInstance().DisAgreeRequest(position);
                    }

                    @Override
                    protected void onPostExecute(Integer aVoid) {
                        super.onPostExecute(aVoid);
                        afterLoading(aVoid);
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
                beforeLoading();
            }

            @Override
            public int middle() {
                return RequestModel.getInstance().loadRequest();
            }

            @Override
            public void after(int code) {
                afterLoading(code);
            }
        }).execute();
    }

    private void beforeLoading(){
        loading.setVisibility(View.VISIBLE);
    }

    private void afterLoading(int code){
        loading.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }


    private void sendBroadCast(int position){
        Intent intent = new Intent(App.RECV_ADD_FRIEND_ACTION);

        AddFriendBean addFriendBean = new AddFriendBean(
                AccountModel.getInstance().currentUser.id,
                AccountModel.getInstance().currentUser.nickname,
                AccountModel.getInstance().currentUser.headImgUrl,
                RequestModel.getInstance().addFriendBeans.get(position).sendId);
        intent.putExtra("data", new Gson().toJson(addFriendBean));
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent);
        RequestModel.getInstance().addFriendBeans.remove(position);
        RequestModel.getInstance().actListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RequestModel.getInstance().listeners.remove(modelListener);
    }
}
