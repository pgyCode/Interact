package com.example.rtyui.mvptalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.tool.MyImgShow;

/**
 * Created by rtyui on 2018/5/17.
 */

public class FriendPageAdapter extends BaseAdapter {

    private Context context;
    private OnActionListener onActionListener;
    public FriendPageAdapter(Context context, OnActionListener onActionListener){
        this.context = context;
        this.onActionListener = onActionListener;
    }
    @Override
    public int getCount() {
        if (RequestModel.getInstance().addFriendBeans == null)
            return 0;
        return RequestModel.getInstance().addFriendBeans.size();
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
        MyImgShow.showNetImgCircle(context, RequestModel.getInstance().addFriendBeans.get(position).sendHeadImgUrl,((ImageView)convertView.findViewById(R.id.img_head)));
        ((TextView)convertView.findViewById(R.id.txt_nickname)).setText(RequestModel.getInstance().addFriendBeans.get(position).sendNickname);
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
        ((TextView)convertView.findViewById(R.id.txt_talkid)).setText("id：" + RequestModel.getInstance().addFriendBeans.get(position).sendId);
        return convertView;
    }

    public interface OnActionListener{
        void doAgree(int position);
        void doDisAgree(int position);
    }
}
