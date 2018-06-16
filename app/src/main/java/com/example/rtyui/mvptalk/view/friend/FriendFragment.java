package com.example.rtyui.mvptalk.view.friend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.rtyui.androidteach.PullRefreshList.PullRefreshInterface;
import com.example.rtyui.androidteach.PullRefreshList.PullRefreshListView;
import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.adapter.LinkFriendAdapter;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;
import com.example.rtyui.mvptalk.view.msg.TalkActivity;

import java.lang.ref.WeakReference;

/**
 * Created by rtyui on 2018/3/31.
 */

public class FriendFragment extends Fragment implements View.OnClickListener {

    private View root = null;

    private LinkFriendAdapter linkFriendAdapter = null;

    private RecvBroadcastReceiver broadcastReceiver = null;

    private PullRefreshListView pullRefreshListView = null;

    private View header = null;

    private OnModelChangeListener modelListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.friend, null);
        linkFriendAdapter = new LinkFriendAdapter(FriendFragment.this.getContext());

        pullRefreshListView = root.findViewById(R.id.listview);

        header = inflater.inflate(R.layout.friend_header, pullRefreshListView, false);
        header.findViewById(R.id.btn_add_new_friend).setOnClickListener(this);
        pullRefreshListView.addHeaderView(header, null, false);

        pullRefreshListView.setAdapter(linkFriendAdapter);

        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                linkFriendAdapter.notifyDataSetChanged();
            }
        };
        FriendModel.getInstance().listeners.add(modelListener);

        pullRefreshListView.setPullRefreshInterface(new PullRefreshInterface() {
            @Override
            public void beforeLoad_PullRefresh() {

            }

            @Override
            public boolean load_PullRefresh() {
                int a = FriendModel.getInstance().flush();
                int b = RequestModel.getInstance().loadRequest();
                return a == App.NET_SUCCEED && b == App.NET_SUCCEED;
            }

            @Override
            public void afterLoad_PullRefresh(boolean result) {
                FriendModel.getInstance().actListeners();
                RequestModel.getInstance().actListeners();
            }
        });

        pullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FriendFragment.this.getContext(), TalkActivity.class);
                intent.putExtra("userId", FriendModel.getInstance().linkFriends.get(position - pullRefreshListView.getHeaderViewsCount()).id);
                intent.putExtra("nickname", FriendModel.getInstance().linkFriends.get(position - pullRefreshListView.getHeaderViewsCount()).nickname);
                intent.putExtra("headImgUrl", FriendModel.getInstance().linkFriends.get(position - pullRefreshListView.getHeaderViewsCount()).headImgUrl);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        broadcastReceiver = new RecvBroadcastReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this.getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(App.LINK_FRIEND_RESPONSE_RECV_OK);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this.getContext());
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }
    public void goAddFriend() {
        startActivity(new Intent(this.getContext(), AddFriendActivity.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_new_friend:
                goAddFriend();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FriendModel.getInstance().listeners.remove(modelListener);
    }

    private class RecvBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case App.LINK_FRIEND_RESPONSE_RECV_OK:
                    linkFriendAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
