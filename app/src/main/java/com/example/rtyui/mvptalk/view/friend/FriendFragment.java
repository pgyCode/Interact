package com.example.rtyui.mvptalk.view.friend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Trace;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.adapter.FriendAdapter;
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

public class FriendFragment extends Fragment{

    private View root = null;

    private FriendAdapter friendAdapter = null;

    private SwipeRefreshLayout refresher = null;

    private ListView listView = null;

    private OnModelChangeListener modelListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.friend, null);
        friendAdapter = new FriendAdapter(FriendFragment.this.getContext());

        listView = root.findViewById(R.id.listview);

        refresher = root.findViewById(R.id.refresher);

        listView.setAdapter(friendAdapter);

        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                friendAdapter.notifyDataSetChanged();
            }
        };
        FriendModel.getInstance().listeners.add(modelListener);

        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new NetTaskCode(new NetTaskCodeListener() {
                    @Override
                    public void before() {

                    }

                    @Override
                    public int middle() {
                        int a = FriendModel.getInstance().flush();
                        int b = RequestModel.getInstance().loadRequest();
                        return a;
                    }

                    @Override
                    public void after(int code) {
                        refresher.setRefreshing(false);
                        if (code == App.NET_SUCCEED) {
                            Toast.makeText(FriendFragment.this.getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
                            FriendModel.getInstance().actListeners();
                            RequestModel.getInstance().actListeners();
                        }else{
                            Toast.makeText(FriendFragment.this.getContext(), "刷新失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).execute();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FriendFragment.this.getContext(), TalkActivity.class);
                intent.putExtra("userId", FriendModel.getInstance().linkFriends.get(position - listView.getHeaderViewsCount()).id);
                intent.putExtra("nickname", FriendModel.getInstance().linkFriends.get(position - listView.getHeaderViewsCount()).nickname);
                intent.putExtra("headImgUrl", FriendModel.getInstance().linkFriends.get(position - listView.getHeaderViewsCount()).headImgUrl);
                startActivity(intent);
            }
        });


        root.findViewById(R.id.btn_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FriendFragment.this.getActivity(), AddFriendActivity.class));
            }
        });
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FriendModel.getInstance().listeners.remove(modelListener);
    }
}
