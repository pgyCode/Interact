package com.example.rtyui.mvptalk.view.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.tool.MyImgShow;

public class ImgShowActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_img_show);
        MyImgShow.showCompleteImgSquare(this, getIntent().getStringExtra("path"), ((ImageView)findViewById(R.id.img)));
    }
}
