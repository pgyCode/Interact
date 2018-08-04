package com.example.rtyui.mvptalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.bean.ComBean;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.tool.App;
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
         return MsgModel.getInstance().LOCAL_getSize();
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
        ComBean comBean = MsgModel.getInstance().comBeans.get(position);
        ChatBean chatBean = comBean.chats.get(comBean.chats.size() - 1);
        if (chatBean.category == App.CATEGORY_FRIEND){
            MyImgShow.showNetImgCircle(context, FriendModel.getInstance().getUserById(MsgModel.getInstance().comBeans.get(position).id).headImgUrl, viewHolder.imgHead);
            viewHolder.txtNick.setText(FriendModel.getInstance().getUserById(MsgModel.getInstance().comBeans.get(position).id).remark);
        }else if (chatBean.category == App.CATEGORY_TEAM){
            MyImgShow.showNetImgCircle(context, TeamModel.getInstance().OUTER_getTeamById(comBean.id).headImgUrl, viewHolder.imgHead);
            viewHolder.txtNick.setText(TeamModel.getInstance().OUTER_getTeamById(comBean.id).nickname);
        }

        if (MsgModel.getInstance().getUnreadByPosition(position) == 0)
            viewHolder.txtCount.setVisibility(View.INVISIBLE);
        else{
            viewHolder.txtCount.setText(MsgModel.getInstance().getUnreadByPosition(position) + "");
            viewHolder.txtCount.setVisibility(View.VISIBLE);
        }
        viewHolder.txtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(MsgModel.getInstance().comBeans.get(position).chats.get(MsgModel.getInstance().comBeans.get(position).chats.size() - 1).time));
        System.out.println(MsgModel.getInstance().comBeans.get(position).id);
        String temp = MsgModel.getInstance().comBeans.get(position).chats.get(MsgModel.getInstance().comBeans.get(position).chats.size() - 1).getMsg();

        if (temp.startsWith(App.MSG_CHAT))
            viewHolder.txtLastMsg.setText(temp.replace(App.MSG_CHAT, ""));
        else if (temp.startsWith(App.MSG_IMG))
            viewHolder.txtLastMsg.setText("[图片]");
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
