package com.example.rtyui.mvptalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.model.TeamRequestModel;
import com.example.rtyui.mvptalk.tool.MyImgShow;

/**
 * Created by rtyui on 2018/5/17.
 */

public class TeamAskerAdapter extends BaseAdapter {

    private Context context;
    private OnActionListener onActionListener;
    public TeamAskerAdapter(Context context, OnActionListener onActionListener){
        this.context = context;
        this.onActionListener = onActionListener;
    }
    @Override
    public int getCount() {
        if (TeamRequestModel.getInstance().askerBeans == null)
            return 0;
        return TeamRequestModel.getInstance().askerBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.link_friend_newfriend_item, null);
        MyImgShow.showNetImgCircle(context, TeamRequestModel.getInstance().askerBeans.get(position).userHeadImgUrl,((ImageView)convertView.findViewById(R.id.img_head)));
        ((TextView)convertView.findViewById(R.id.txt_nickname)).setText(TeamRequestModel.getInstance().askerBeans.get(position).userName);
        convertView.findViewById(R.id.btn_agree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionListener.doAgree(position);
            }
        });
        convertView.findViewById(R.id.btn_disagree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionListener.doDisAgree(position);
            }
        });
        ((TextView)convertView.findViewById(R.id.txt_talkid)).setText("目标：" + TeamRequestModel.getInstance().askerBeans.get(position).tname);
        return convertView;
    }

    public interface OnActionListener{
        void doAgree(int position);
        void doDisAgree(int position);
    }
}
