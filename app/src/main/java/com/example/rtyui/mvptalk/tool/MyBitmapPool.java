package com.example.rtyui.mvptalk.tool;

import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

/**
 * Created by rtyui on 2018/4/25.
 */

public class MyBitmapPool implements BitmapPool {
    @Override
    public int getMaxSize() {
        return 0;
    }

    @Override
    public void setSizeMultiplier(float v) {

    }

    @Override
    public boolean put(Bitmap bitmap) {
        return false;
    }

    @Override
    public Bitmap get(int i, int i1, Bitmap.Config config) {
        return null;
    }

    @Override
    public Bitmap getDirty(int i, int i1, Bitmap.Config config) {
        return null;
    }

    @Override
    public void clearMemory() {

    }

    @Override
    public void trimMemory(int i) {

    }
}
