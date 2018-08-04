package com.example.rtyui.mvptalk.view.team;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.adapter.TeamAdapter;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;
import com.example.rtyui.mvptalk.view.friend.AddFriendActivity;
import com.example.rtyui.mvptalk.view.friend.FriendFragment;
import com.example.rtyui.mvptalk.view.msg.TalkActivity;
import com.example.rtyui.mvptalk.view.msg.TeamTalkActivity;

/**
 * Created by rtyui on 2018/3/31.
 */

public class TeamFragment extends Fragment {

    private View root = null;

    private TeamAdapter teamAdapter = null;

    private SwipeRefreshLayout refresher = null;

    private ListView listView = null;

//    private View header = null;

    private OnModelChangeListener modelListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.team, null);
        teamAdapter = new TeamAdapter(TeamFragment.this.getContext());

        listView = root.findViewById(R.id.listview);

        refresher = root.findViewById(R.id.refresher);

        listView.setAdapter(teamAdapter);

        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new NetTaskCode(new NetTaskCodeListener() {
                    @Override
                    public void before() {

                    }

                    @Override
                    public int middle() {
                        return TeamModel.getInstance().NET_flushTeam();
                    }

                    @Override
                    public void after(int code) {
                        refresher.setRefreshing(false);
                        if (code == App.NET_SUCCEED) {
                            Toast.makeText(TeamFragment.this.getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
                            TeamModel.getInstance().actListeners();
                        }
                        else{
                            Toast.makeText(TeamFragment.this.getContext(), "刷新失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).execute();
            }
        });

        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                teamAdapter.notifyDataSetChanged();
            }
        };
        TeamModel.getInstance().listeners.add(modelListener);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TeamFragment.this.getContext(), TeamTalkActivity.class);
                intent.putExtra("id", TeamModel.getInstance().teamBeans.get(position - listView.getHeaderViewsCount()).id);
                startActivity(intent);
            }
        });


        root.findViewById(R.id.btn_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeamFragment.this.getContext(), AddTeamActivity.class));
            }
        });
        return root;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        TeamModel.getInstance().listeners.remove(modelListener);
    }
}
