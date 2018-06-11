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
import com.example.rtyui.mvptalk.tool.MyImgShow;

/**
 * Created by rtyui on 2018/5/19.
 */

public class LinkFriendAdapter extends BaseAdapter {



    private Context context;

    public LinkFriendAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        if (FriendModel.getInstance().linkFriends == null)
            return 0;
        return FriendModel.getInstance().linkFriends.size();
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
        FriendHolder viewHolder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.friend_item, null,false);
            viewHolder = new FriendHolder();
            viewHolder.userName = convertView.findViewById(R.id.txt_username);
            viewHolder.headImg = convertView.findViewById(R.id.img_head);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (FriendHolder) convertView.getTag();
        }
        viewHolder.userName.setText(FriendModel.getInstance().linkFriends.get(position).nickname);
        MyImgShow.showNetImgCircle(context, FriendModel.getInstance().linkFriends.get(position).headImgUrl, viewHolder.headImg);
        return convertView;
    }


    private class FriendHolder{
        ImageView headImg;
        TextView userName;
    }
}
