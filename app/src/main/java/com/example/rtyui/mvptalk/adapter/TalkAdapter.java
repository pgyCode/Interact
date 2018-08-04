package com.example.rtyui.mvptalk.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MyImgShow;
import com.example.rtyui.mvptalk.view.common.ImgShowActivity;

import java.text.SimpleDateFormat;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by rtyui on 2018/4/2.
 */

public class TalkAdapter extends BaseAdapter {

    private int id;
    private Context context;

    public TalkAdapter(Context context, int id){
        this.context = context;
        this.id = id;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public int getCount() {
        if (MsgModel.getInstance().getFriendCombeanById(id) == null)
            return 0;
        return MsgModel.getInstance().getFriendCombeanById(id).chats.size();
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
        final ChatBean bean = MsgModel.getInstance().getFriendCombeanById(id).chats.get(position);
        //自己发出的消息
        if (bean.sendId == AccountModel.getInstance().currentUser.id){
            if (bean.msg.startsWith(App.MSG_CHAT)){
                convertView = LayoutInflater.from(context).inflate(R.layout.talk_my_item, null, false);
                TextView txtMsg = convertView.findViewById(R.id.txt_msg);
                ImageView imgHead = convertView.findViewById(R.id.img_head);
                ImageView imgStatu = convertView.findViewById(R.id.img_statu);
                TextView txtTime = convertView.findViewById(R.id.txt_time);
                txtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(bean.time));
                if (position == 0 || bean.time - MsgModel.getInstance().getFriendCombeanById(id).chats.get(position - 1).time > App.TALK_TIME_SPACE){
                    txtTime.setVisibility(View.VISIBLE);
                }else{
                    txtTime.setVisibility(View.GONE);
                }
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
                convertView = LayoutInflater.from(context).inflate(R.layout.talk_my_img, null, false);
                ImageView imgContent = convertView.findViewById(R.id.img_content);
                ImageView imgHead = convertView.findViewById(R.id.img_head);
                ImageView imgStatu = convertView.findViewById(R.id.img_statu);
                TextView txtTime = convertView.findViewById(R.id.txt_time);
                txtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(bean.time));
                if (position == 0 || bean.time - MsgModel.getInstance().getFriendCombeanById(id).chats.get(position - 1).time > App.TALK_TIME_SPACE){
                    txtTime.setVisibility(View.VISIBLE);
                }else{
                    txtTime.setVisibility(View.GONE);
                }
                MyImgShow.showNetImgSquare(context, bean.msg.replace(App.MSG_IMG, ""), imgContent);

                imgContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ImgShowActivity.class);
                        intent.putExtra("sign", App.PHOTO_SHOW_SIGN_SENDIMG);
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
                convertView = LayoutInflater.from(context).inflate(R.layout.talk_other_item, null, false);
                TextView txtMsg = convertView.findViewById(R.id.txt_msg);
                ImageView imgHead = convertView.findViewById(R.id.img_head);
                txtMsg.setText(bean.msg.replace(App.MSG_CHAT, ""));
                TextView txtTime = convertView.findViewById(R.id.txt_time);
                txtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(bean.time));
                if (position == 0 || bean.time - MsgModel.getInstance().getFriendCombeanById(id).chats.get(position - 1).time > App.TALK_TIME_SPACE){
                    txtTime.setVisibility(View.VISIBLE);
                }else{
                    txtTime.setVisibility(View.GONE);
                }
                MyImgShow.showNetImgCircle(context, FriendModel.getInstance().getUserById(bean.sendId).headImgUrl, imgHead);
            }
            else if(bean.msg.startsWith(App.MSG_IMG)){
                convertView = LayoutInflater.from(context).inflate(R.layout.talk_other_img, null, false);
                ImageView imgContent = convertView.findViewById(R.id.img_content);
                ImageView imgHead = convertView.findViewById(R.id.img_head);
                MyImgShow.showNetImgSquare(context, bean.msg.replace(App.MSG_IMG, ""), imgContent);
                imgContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ImgShowActivity.class);
                        intent.putExtra("sign", App.PHOTO_SHOW_SIGN_SENDIMG);
                        intent.putExtra("id", id);
                        intent.putExtra("position", position);
                        context.startActivity(intent);
                    }
                });
                TextView txtTime = convertView.findViewById(R.id.txt_time);
                txtTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(bean.time));
                if (position == 0 || bean.time - MsgModel.getInstance().getFriendCombeanById(id).chats.get(position - 1).time > App.TALK_TIME_SPACE){
                    txtTime.setVisibility(View.VISIBLE);
                }else{
                    txtTime.setVisibility(View.GONE);
                }
                MyImgShow.showNetImgCircle(context, FriendModel.getInstance().getUserById(bean.sendId).headImgUrl, imgHead);
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
