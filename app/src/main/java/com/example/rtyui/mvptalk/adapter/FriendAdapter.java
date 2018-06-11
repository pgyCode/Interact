package com.example.rtyui.mvptalk.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.FriendModel;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by rtyui on 2018/4/1.
 */

public class FriendAdapter
//        extends BaseExpandableListAdapter implements BitmapPool
{

    private Context context;
    private Fragment fragment = null;
    private ExpandableListView expandableListView = null;
//
//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case 100002:
//                    notifyDataSetChanged();
//                    if (FriendModel.getInstance().groupBeans != null){
//                        for (int i = 0; i < FriendModel.getInstance().groupBeans.size(); i++){
//                            expandableListView.collapseGroup(i);
//                            expandableListView.expandGroup(i);
//                        }
//                    }
//                    break;
//            }
//        }
//    };
//
//    public void MyNotifyDataSetChanged(){
//        handler.sendEmptyMessage(100002);
//    }
//
//    public FriendAdapter(Context context, Fragment fragment, ExpandableListView expandableListView){
//        this.context = context;
//        this.fragment = fragment;
//        this.expandableListView = expandableListView;
//    }
//
//    @Override
//    public int getMaxSize() {
//        return 0;
//    }
//
//    @Override
//    public void setSizeMultiplier(float v) {
//
//    }
//
//    @Override
//    public boolean put(Bitmap bitmap) {
//        return false;
//    }
//
//    @Override
//    public Bitmap get(int i, int i1, Bitmap.Config config) {
//        return null;
//    }
//
//    @Override
//    public Bitmap getDirty(int i, int i1, Bitmap.Config config) {
//        return null;
//    }
//
//    @Override
//    public void clearMemory() {
//
//    }
//
//    @Override
//    public void trimMemory(int i) {
//
//    }
//
//    @Override
//    public void registerDataSetObserver(DataSetObserver observer) {
//
//    }
//
//    @Override
//    public void unregisterDataSetObserver(DataSetObserver observer) {
//
//    }
//
//    @Override
//    public int getGroupCount() {
//        if (FriendModel.getInstance().groupBeans == null)
//            return 0;
//        return FriendModel.getInstance().groupBeans.size();
//    }
//
//    @Override
//    public int getChildrenCount(int groupPosition) {
//        if (FriendModel.getInstance().groupBeans.get(groupPosition).friends == null)
//            return 0;
//        return FriendModel.getInstance().groupBeans.get(groupPosition).friends.size();
//    }
//
//    @Override
//    public Object getGroup(int groupPosition) {
//        return null;
//    }
//
//    @Override
//    public Object getChild(int groupPosition, int childPosition) {
//        return null;
//    }
//
//    @Override
//    public long getGroupId(int groupPosition) {
//        return groupPosition;
//    }
//
//    @Override
//    public long getChildId(int groupPosition, int childPosition) {
//        return childPosition;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return false;
//    }
//
//    @Override
//    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//        GroupHolder viewHolder = null;
//        if (convertView == null){
//            convertView = LayoutInflater.from(context).inflate(R.layout.group_item, null, false);
//            viewHolder = new GroupHolder();
//            viewHolder.GroupName = convertView.findViewById(R.id.txt_groupname);
//            convertView.setTag(viewHolder);
//        }else{
//            viewHolder = (GroupHolder) convertView.getTag();
//        }
//        viewHolder.GroupName.setText(FriendModel.getInstance().groupBeans.get(groupPosition).group.groupName);
//        return convertView;
//    }
//
//    @Override
//    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        FriendHolder viewHolder = null;
//        if (convertView == null){
//            convertView = LayoutInflater.from(context).inflate(R.layout.friend_item, null,false);
//            viewHolder = new FriendHolder();
//            viewHolder.userName = convertView.findViewById(R.id.txt_username);
//            viewHolder.headImg = convertView.findViewById(R.id.img_head);
//            convertView.setTag(viewHolder);
//        }else{
//            viewHolder = (FriendHolder) convertView.getTag();
//        }
//        viewHolder.userName.setText(FriendModel.getInstance().groupBeans.get(groupPosition).friends.get(childPosition).nickname);
//        Glide.with(fragment)
//                .load(FriendModel.getInstance().groupBeans.get(groupPosition).friends.get(childPosition).headImgUrl)
//                .error(R.mipmap.ic_launcher)
//                .placeholder(R.mipmap.ic_launcher)
//                .bitmapTransform(new CropCircleTransformation(this))
//                .into(viewHolder.headImg);
//        return convertView;
//    }
//
//    @Override
//    public boolean isChildSelectable(int groupPosition, int childPosition) {
//        return true;
//    }
//
//    private class GroupHolder{
//        TextView GroupName;
//    }
//    private class FriendHolder{
//        ImageView headImg;
//        TextView userName;
//    }
}