package com.example.rtyui.mvptalk.view.msg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import com.example.rtyui.androidteach.PullRefreshList.PullRefreshInterface;
import com.example.rtyui.androidteach.PullRefreshList.PullRefreshListView;
import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.adapter.MsgAdapter;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.model.TeamMsgModel;
import com.example.rtyui.mvptalk.newBean.TeamMemberBean;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;

import java.lang.ref.WeakReference;


/**
 * Created by rtyui on 2018/3/31.
 */

public class MsgFragment extends Fragment{

    private View root = null;

    private MsgAdapter msgAdapter;

    private PullRefreshListView listView;

    private OnModelChangeListener modelListener;

    private PopupWindow popMsg;
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
        TeamMsgModel.getInstance().listeners.add(modelListener);

        return root;
    }

    private void initLayout() {
        initPop();
        listView = root.findViewById(R.id.lst);
        listView.setAdapter(msgAdapter = new MsgAdapter(this.getContext()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goTalk(position - listView.getHeaderViewsCount());
            }
        });
        listView.setPullRefreshInterface(new PullRefreshInterface() {
            @Override
            public void beforeLoad_PullRefresh() {

            }

            @Override
            public boolean load_PullRefresh() {
                int temp = MsgModel.getInstance().doFlush();
                if (temp == 2){
                    return true;
                }else{
                    return false;
                }
            }

            @Override
            public void afterLoad_PullRefresh(boolean result) {
                MsgModel.getInstance().actListeners();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void goTalk(int position) {
        if (position < MsgModel.getInstance().LOCAL_getSize()){
            Intent intent = new Intent(MsgFragment.this.getContext(), TalkActivity.class);
            intent.putExtra("userId", MsgModel.getInstance().comBeans.get(position).userId);
            //intent.putExtra("nickname", MsgModel.getInstance().comBeans.get(position).nickname);
            //intent.putExtra("headImgUrl", MsgModel.getInstance().comBeans.get(position).headImgUrl);
            startActivity(intent);
        }else{
            Intent intent = new Intent(MsgFragment.this.getContext(), TeamTalkActivity.class);
            intent.putExtra("id", TeamMsgModel.getInstance().comBeans.get(position - MsgModel.getInstance().LOCAL_getSize()).id);
            //intent.putExtra("nickname", MsgModel.getInstance().comBeans.get(position).nickname);
            //intent.putExtra("headImgUrl", MsgModel.getInstance().comBeans.get(position).headImgUrl);
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
        TeamMsgModel.getInstance().listeners.remove(modelListener);
    }

    /**
     * 初始化popupwindow
     */
    private void initPop(){
        View viewPop = LayoutInflater.from(this.getActivity()).inflate(R.layout.msg_pop, null);

        popMsg = new PopupWindow();
        viewPop.measure(0, 0);
        popMsg.setHeight(viewPop.getMeasuredHeight());
        popMsg.setWidth(viewPop.getMeasuredWidth());
        popMsg.setContentView(viewPop);
        popMsg.setOutsideTouchable(true);
        popMsg.setFocusable(true);
        popMsg.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popMsg.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
    }

    /**
     * 设置背景透明度
     * @param bgAlpha
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getActivity().getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        this.getActivity().getWindow().setAttributes(lp);
    }
}
