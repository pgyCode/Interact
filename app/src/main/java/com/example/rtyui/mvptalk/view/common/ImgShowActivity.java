package com.example.rtyui.mvptalk.view.common;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.tool.AbstractNetTaskCode;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MyImgShow;
import com.example.rtyui.mvptalk.tool.NetVisitor;
import com.example.rtyui.mvptalk.view.msg.TalkActivity;

import java.util.ArrayList;
import java.util.List;

public class ImgShowActivity extends Activity {

    private ViewPager viewPager;

    private MyAdapter myAdapter;

    private int id;

    private List<View> views;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_img_show);

        views = new ArrayList<>();

        id = getIntent().getIntExtra("id", -1);

        viewPager = findViewById(R.id.viewpager);

        viewPager.setAdapter(myAdapter = new MyAdapter());

        Toast.makeText(this, getIntent().getIntExtra("position", -1) + "", Toast.LENGTH_SHORT).show();

        viewPager.setCurrentItem(MsgModel.getInstance().getImgPosition(getIntent().getIntExtra("position", -1), id));

        for (int i = 0; i < MsgModel.getInstance().getImgCount(id); i++){
            View view = LayoutInflater.from(ImgShowActivity.this).inflate(R.layout.common_img_show_item, null, false);
            MyImgShow.showCompleteImgSquare(ImgShowActivity.this, MsgModel.getInstance().getCombeanById(id).chats.get(MsgModel.getInstance().getMsgPosition(id, i)).msg.replace(App.MSG_IMG, ""), ((ImageView)view.findViewById(R.id.img)));
            views.add(view);
        }

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(ImgShowActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ImgShowActivity.this.requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                    }else {
                        Toast.makeText(ImgShowActivity.this, "权限已申请", Toast.LENGTH_SHORT).show();
                        new ImgDownloadAsyncTask().execute();
                    }
                }
                else{
                    new ImgDownloadAsyncTask().execute();
                }
            }
        });
    }


    public class MyAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return MsgModel.getInstance().getImgCount(id);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            if (views.get(position).getParent() == null)
                container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        }
    }

    public class ImgDownloadAsyncTask extends AbstractNetTaskCode{

        @Override
        protected void before() {

        }

        @Override
        protected int middle() {
            return NetVisitor.downloadImg(MsgModel.getInstance().getCombeanById(id).chats.get(MsgModel.getInstance().getMsgPosition(id, viewPager.getCurrentItem())).msg.replace(App.MSG_IMG, ""), System.currentTimeMillis() + "");
        }

        @Override
        protected void after(int integer) {
            if (integer == App.NET_SUCCEED){
                Toast.makeText(ImgShowActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ImgShowActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
