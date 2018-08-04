package com.example.rtyui.mvptalk.tool;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rtyui.mvptalk.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by rtyui on 2018/4/25.
 */

public class MyImgShow {

    public static void showNetImgCircle(Context context, String url, ImageView img){
        Glide.with(context)
                .load(url)
                .error(R.mipmap.ic_launcher_round)
                .placeholder(R.mipmap.ic_launcher_round)
                .override(140, 140)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(img);
    }


    public static void showLocalImgSquare(Context context, String url, ImageView img){
        Glide.with(context)
                .load(url)
                .error(R.mipmap.ic_launcher_round)
                .placeholder(R.mipmap.ic_launcher_round)
                .dontAnimate()
                .override(200, 200)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .fitCenter()
                .into(img);
    }

    public static void showCompleteImgSquare(Context context, String url, ImageView img){
        Glide.with(context)
                .load(url)
                .error(R.mipmap.ic_launcher_round)
                .placeholder(R.mipmap.ic_launcher_round)
                .dontAnimate()
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .fitCenter()
                .into(img);
    }

    public static void showNetImgCircle(Fragment fragment, String url, ImageView img){
        Glide.with(fragment)
                .load(url)
                .error(R.mipmap.ic_launcher_round)
                .override(140, 140)
                .placeholder(R.mipmap.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(img);
    }

    public static void showNetImgSquare(Context context, String url, ImageView img){
        Glide.with(context)
                .load(url)
                .dontAnimate()
                .override(100, 75)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(img);
    }

    public static void showNetImgCircle(Activity activity, String url, ImageView img){
        Glide.with(activity)
                .load(url)
                .error(R.mipmap.ic_launcher_round)
                .override(140, 140)
                .placeholder(R.mipmap.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(img);
    }

    public static void showNetImgCircle(FragmentActivity activity, String url, ImageView img){
        Glide.with(activity)
                .load(url)
                .error(R.mipmap.ic_launcher_round)
                .override(140, 140)
                .placeholder(R.mipmap.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(img);
    }

}
