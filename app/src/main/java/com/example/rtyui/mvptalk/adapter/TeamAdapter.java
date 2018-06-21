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
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.newBean.TeamBean;
import com.example.rtyui.mvptalk.tool.MyImgShow;

/**
 * Created by rtyui on 2018/5/19.
 */

public class TeamAdapter extends BaseAdapter {



    private Context context;

    public TeamAdapter(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        if (TeamModel.getInstance().teamBeans == null)
            return 0;
        return TeamModel.getInstance().teamBeans.size();
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
        TeamHolder viewHolder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.friend_item, null,false);
            viewHolder = new TeamHolder();
            viewHolder.userName = convertView.findViewById(R.id.txt_username);
            viewHolder.headImg = convertView.findViewById(R.id.img_head);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (TeamHolder) convertView.getTag();
        }
        viewHolder.userName.setText(TeamModel.getInstance().teamBeans.get(position).nickname);
        MyImgShow.showNetImgCircle(context, TeamModel.getInstance().teamBeans.get(position).headImgUrl, viewHolder.headImg);
        return convertView;
    }


    private class TeamHolder{
        ImageView headImg;
        TextView userName;
    }
}
