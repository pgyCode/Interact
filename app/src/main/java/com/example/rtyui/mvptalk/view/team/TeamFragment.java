package com.example.rtyui.mvptalk.view.team;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.rtyui.androidteach.PullRefreshList.PullRefreshInterface;
import com.example.rtyui.androidteach.PullRefreshList.PullRefreshListView;
import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.adapter.LinkFriendAdapter;
import com.example.rtyui.mvptalk.adapter.TeamAdapter;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.view.friend.AddFriendActivity;
import com.example.rtyui.mvptalk.view.msg.TalkActivity;
import com.example.rtyui.mvptalk.view.msg.TeamTalkActivity;

/**
 * Created by rtyui on 2018/3/31.
 */

public class TeamFragment extends Fragment implements View.OnClickListener {

    private View root = null;

    private TeamAdapter teamAdapter = null;

    private PullRefreshListView pullRefreshListView = null;

    private View header = null;

    private OnModelChangeListener modelListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.team, null);
        teamAdapter = new TeamAdapter(TeamFragment.this.getContext());

        pullRefreshListView = root.findViewById(R.id.listview);

        header = inflater.inflate(R.layout.team_header, pullRefreshListView, false);
        header.findViewById(R.id.btn_add_team).setOnClickListener(this);
        header.findViewById(R.id.btn_create_team).setOnClickListener(this);

        pullRefreshListView.addHeaderView(header, null, false);

        pullRefreshListView.setAdapter(teamAdapter);

        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                teamAdapter.notifyDataSetChanged();
            }
        };
        //FriendModel.getInstance().listeners.add(modelListener);

        pullRefreshListView.setPullRefreshInterface(new PullRefreshInterface() {
            @Override
            public void beforeLoad_PullRefresh() {

            }

            @Override
            public boolean load_PullRefresh() {
                //int a = FriendModel.getInstance().flush();
                //int b = RequestModel.getInstance().loadRequest();
                //return a == App.NET_SUCCEED && b == App.NET_SUCCEED;
                return false;
            }

            @Override
            public void afterLoad_PullRefresh(boolean result) {
                //FriendModel.getInstance().actListeners();
                //RequestModel.getInstance().actListeners();
            }
        });

        pullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TeamFragment.this.getContext(), TeamTalkActivity.class);
                intent.putExtra("id", TeamModel.getInstance().teamBeans.get(position - pullRefreshListView.getHeaderViewsCount()).id);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_team:
                startActivity(new Intent(this.getContext(), TeamAddActivity.class));
                break;
            case R.id.btn_create_team:
                startActivity(new Intent(this.getContext(), TeamCreateActivity.class));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FriendModel.getInstance().listeners.remove(modelListener);
    }
}
