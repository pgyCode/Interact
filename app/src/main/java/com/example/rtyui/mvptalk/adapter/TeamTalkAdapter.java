package com.example.rtyui.mvptalk.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.model.TeamMsgModel;
import com.example.rtyui.mvptalk.newBean.TeamChatBean;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MyImgShow;
import com.example.rtyui.mvptalk.view.common.ImgShowActivity;

import java.text.SimpleDateFormat;

/**
 * Created by rtyui on 2018/4/2.
 */

public class TeamTalkAdapter extends BaseAdapter {

    private int id;
    private Context context;

    public TeamTalkAdapter(Context context, int id){
        this.context = context;
        this.id = id;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        if (TeamMsgModel.getInstance().getCombeanById(id) == null)
            return 0;
        return TeamMsgModel.getInstance().getCombeanById(id).chats.size();
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

        //ViewHolder viewHolder = null;
        final TeamChatBean bean = TeamMsgModel.getInstance().getCombeanById(id).chats.get(position);
        //自己发出的消息
        if (bean.sendId == AccountModel.getInstance().currentUser.id){
            if (bean.msg.startsWith(App.MSG_CHAT)){
                convertView = LayoutInflater.from(context).inflate(R.layout.team_talk_my_item, null, false);
                TextView txtMsg = convertView.findViewById(R.id.txt_msg);
                ImageView imgHead = convertView.findViewById(R.id.img_head);
                ImageView imgStatu = convertView.findViewById(R.id.img_statu);
                TextView txtTime = convertView.findViewById(R.id.txt_time);
                TextView txtNick = convertView.findViewById(R.id.txt_nick);
                txtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(bean.time));
                if (position == 0 || bean.time - TeamMsgModel.getInstance().getCombeanById(id).chats.get(position - 1).time > App.TALK_TIME_SPACE){
                    txtTime.setVisibility(View.VISIBLE);
                }else{
                    txtTime.setVisibility(View.GONE);
                }
                txtNick.setText(AccountModel.getInstance().currentUser.nickname);
                txtMsg.setText(bean.msg.replace(App.MSG_CHAT, ""));
                MyImgShow.showNetImgCircle(context, AccountModel.getInstance().currentUser.headImgUrl, imgHead);
                switch (bean.statu){
                    case App.MSG_SEND_ING:
                        imgStatu.setVisibility(View.VISIBLE);
                        imgStatu.setImageResource(R.drawable.loading_anim);
                        AnimationDrawable drawable = (AnimationDrawable) imgStatu.getDrawable();
                        drawable.start();
                        break;
                    case App.MSG_SEND_GOOD:
                        imgStatu.setVisibility(View.GONE);
                        break;
                    case App.MSG_SEND_BAD:
                        imgStatu.setVisibility(View.VISIBLE);
                        imgStatu.setImageResource(R.drawable.send_fail);
                }
            }
            else if(bean.msg.startsWith(App.MSG_IMG)){
                convertView = LayoutInflater.from(context).inflate(R.layout.team_talk_my_img, null, false);
                ImageView imgContent = convertView.findViewById(R.id.img_content);
                ImageView imgHead = convertView.findViewById(R.id.img_head);
                ImageView imgStatu = convertView.findViewById(R.id.img_statu);
                TextView txtTime = convertView.findViewById(R.id.txt_time);
                TextView txtNick = convertView.findViewById(R.id.txt_nick);
                txtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(bean.time));
                if (position == 0 || bean.time - TeamMsgModel.getInstance().getCombeanById(id).chats.get(position - 1).time > App.TALK_TIME_SPACE){
                    txtTime.setVisibility(View.VISIBLE);
                }else{
                    txtTime.setVisibility(View.GONE);
                }
                txtNick.setText(AccountModel.getInstance().currentUser.nickname);
                MyImgShow.showNetImgSquare(context, bean.msg.replace(App.MSG_IMG, ""), imgContent);

                imgContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ImgShowActivity.class);
                        intent.putExtra("sign", App.PHOTO_SHOW_SIGN_TEAM_SENDIMG);
                        intent.putExtra("id", id);
                        intent.putExtra("position", position);
                        context.startActivity(intent);
                    }
                });

                MyImgShow.showNetImgCircle(context, AccountModel.getInstance().currentUser.headImgUrl, imgHead);
                switch (bean.statu){
                    case App.MSG_SEND_ING:
                        imgStatu.setVisibility(View.VISIBLE);
                        imgStatu.setImageResource(R.drawable.loading_anim);
                        AnimationDrawable drawable = (AnimationDrawable) imgStatu.getDrawable();
                        drawable.start();
                        break;
                    case App.MSG_SEND_GOOD:
                        imgStatu.setVisibility(View.GONE);
                        break;
                    case App.MSG_SEND_BAD:
                        imgStatu.setVisibility(View.VISIBLE);
                        imgStatu.setImageResource(R.drawable.send_fail);
                }
            }else{

            }
        }
        //来自别人的消息
        else{
            if (bean.msg.startsWith(App.MSG_CHAT)){
                convertView = LayoutInflater.from(context).inflate(R.layout.team_talk_other_item, null, false);
                TextView txtMsg = convertView.findViewById(R.id.txt_msg);
                ImageView imgHead = convertView.findViewById(R.id.img_head);
                txtMsg.setText(bean.msg.replace(App.MSG_CHAT, ""));
                TextView txtTime = convertView.findViewById(R.id.txt_time);
                txtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(bean.time));
                TextView txtNick = convertView.findViewById(R.id.txt_nick);
                if (position == 0 || bean.time - TeamMsgModel.getInstance().getCombeanById(id).chats.get(position - 1).time > App.TALK_TIME_SPACE){
                    txtTime.setVisibility(View.VISIBLE);
                }else{
                    txtTime.setVisibility(View.GONE);
                }
                txtNick.setText(TeamModel.getInstance().OUTER_getMemberById(id, bean.sendId).nickname);
                MyImgShow.showNetImgCircle(context, TeamModel.getInstance().OUTER_getMemberById(id, bean.sendId).headImgUrl, imgHead);
            }
            else if(bean.msg.startsWith(App.MSG_IMG)){
                convertView = LayoutInflater.from(context).inflate(R.layout.team_talk_other_img, null, false);
                ImageView imgContent = convertView.findViewById(R.id.img_content);
                ImageView imgHead = convertView.findViewById(R.id.img_head);
                MyImgShow.showNetImgSquare(context, bean.msg.replace(App.MSG_IMG, ""), imgContent);
                imgContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ImgShowActivity.class);
                        intent.putExtra("sign", App.PHOTO_SHOW_SIGN_TEAM_SENDIMG);
                        intent.putExtra("id", id);
                        intent.putExtra("position", position);
                        context.startActivity(intent);
                    }
                });
                TextView txtTime = convertView.findViewById(R.id.txt_time);
                txtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(bean.time));
                TextView txtNick = convertView.findViewById(R.id.txt_nick);
                if (position == 0 || bean.time - TeamMsgModel.getInstance().getCombeanById(id).chats.get(position - 1).time > App.TALK_TIME_SPACE){
                    txtTime.setVisibility(View.VISIBLE);
                }else{
                    txtTime.setVisibility(View.GONE);
                }
                txtNick.setText(TeamModel.getInstance().OUTER_getMemberById(id, bean.sendId).nickname);
                MyImgShow.showNetImgCircle(context, TeamModel.getInstance().OUTER_getMemberById(id, bean.sendId).headImgUrl, imgHead);
            }else{

            }
        }
        return convertView;
    }


    private class ViewHolder{
        public boolean mine;
        public TextView txtMsg;
        private ImageView imgHead;
        private ImageView imgStatu;
    }
}
