package com.example.rtyui.mvptalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.bean.ComBean;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.tool.MyImgShow;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by rtyui on 2018/5/2.
 */

public class MsgAdapter extends BaseAdapter {

    private Context context;

    public MsgAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        if (MsgModel.getInstance().comBeans == null){
            return 0;
        }
        return MsgModel.getInstance().comBeans.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.msg_item, null, false);
            viewHolder.imgHead = convertView.findViewById(R.id.img_head);
            viewHolder.txtNick = convertView.findViewById(R.id.txt_nickname);
            viewHolder.txtCount = convertView.findViewById(R.id.txt_num);
            viewHolder.txtLastMsg = convertView.findViewById(R.id.txt_msg);
            viewHolder.txtTime = convertView.findViewById(R.id.txt_time);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MyImgShow.showNetImgCircle(context, FriendModel.getInstance().getUserById(MsgModel.getInstance().comBeans.get(position).userId).headImgUrl, viewHolder.imgHead);
        if (MsgModel.getInstance().comBeans.get(position).unread == 0)
            viewHolder.txtCount.setVisibility(View.INVISIBLE);
        else{
            viewHolder.txtCount.setText(MsgModel.getInstance().comBeans.get(position).unread + "");
            viewHolder.txtCount.setVisibility(View.VISIBLE);
        }
        viewHolder.txtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(MsgModel.getInstance().comBeans.get(position).time));
        System.out.println(MsgModel.getInstance().comBeans.get(position).userId);
        viewHolder.txtNick.setText(FriendModel.getInstance().getUserById(MsgModel.getInstance().comBeans.get(position).userId).nickname);
        viewHolder.txtLastMsg.setText(MsgModel.getInstance().comBeans.get(position).chats.get(MsgModel.getInstance().comBeans.get(position).chats.size() - 1).getMsg());
        return convertView;
    }

    private class ViewHolder{
        public ImageView imgHead;
        public TextView txtNick;
        public TextView txtCount;
        public TextView txtLastMsg;
        public TextView txtTime;
    }
}
