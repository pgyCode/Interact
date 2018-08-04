package com.example.rtyui.mvptalk.view.msg;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.adapter.MsgAdapter;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;

import java.lang.ref.WeakReference;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by rtyui on 2018/3/31.
 */

public class MsgFragment extends Fragment{

    private View root = null;

    private MsgAdapter msgAdapter;

    private ListView listView;

    private OnModelChangeListener modelListener;

    private SwipeRefreshLayout refreshLayout;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.msg, null);

        initLayout();

        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                msgAdapter.notifyDataSetChanged();
            }
        };
        MsgModel.getInstance().listeners.add(modelListener);
        FriendModel.getInstance().listeners.add(modelListener);
        TeamModel.getInstance().listeners.add(modelListener);
        MsgModel.getInstance().listeners.add(modelListener);

        return root;
    }

    private void initLayout() {
        listView = root.findViewById(R.id.lst);
        listView.setAdapter(msgAdapter = new MsgAdapter(this.getContext()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goTalk(position - listView.getHeaderViewsCount());
            }
        });

        refreshLayout = root.findViewById(R.id.root);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new NetTaskCode(new NetTaskCodeListener() {
                    @Override
                    public void before() {

                    }

                    @Override
                    public int middle() {
                        return MsgModel.getInstance().doFlush();
                    }

                    @Override
                    public void after(int code) {
                        refreshLayout.setRefreshing(false);
                        if (code == App.NET_SUCCEED){
                            MsgModel.getInstance().actListeners();
                            Toast.makeText(MsgFragment.this.getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(MsgFragment.this.getContext(), "刷新失败", Toast.LENGTH_SHORT).show();
                    }
                }).execute();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager) this.getActivity().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        MsgModel.getInstance().isNotifi = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        MsgModel.getInstance().isNotifi = true;
    }

    public void goTalk(int position) {
        if (MsgModel.getInstance().comBeans.get(position).category == App.CATEGORY_FRIEND){
            Intent intent = new Intent(MsgFragment.this.getContext(), TalkActivity.class);
            intent.putExtra("userId", MsgModel.getInstance().comBeans.get(position).id);
            startActivity(intent);
        }else{
            Intent intent = new Intent(MsgFragment.this.getContext(), TeamTalkActivity.class);
            intent.putExtra("id", MsgModel.getInstance().comBeans.get(position).id);
            startActivity(intent);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        App.BROADCAST_STATU = App.NO_BROADCAST;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MsgModel.getInstance().listeners.remove(modelListener);
        FriendModel.getInstance().listeners.remove(modelListener);
        TeamModel.getInstance().listeners.remove(modelListener);
        MsgModel.getInstance().listeners.remove(modelListener);
    }
}
