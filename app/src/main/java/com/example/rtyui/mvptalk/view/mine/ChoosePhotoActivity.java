package com.example.rtyui.mvptalk.view.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.example.rtyui.mvptalk.BuildConfig;
import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MyImgShow;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;
import com.example.rtyui.mvptalk.tool.NetTaskSet;
import com.example.rtyui.mvptalk.tool.NetTaskSetListener;
import com.example.rtyui.mvptalk.view.msg.TalkActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;


public class ChoosePhotoActivity extends Activity {

    private ListView lst;

    private List<String> photos;
    private final int CROP_HEADIMG = 100002;

    private ViewStub loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_choose_photo);

        lst = findViewById(R.id.lst);

        loading = findViewById(R.id.loading);

        photos = (List<String>) getIntent().getSerializableExtra("photos");

        lst.setAdapter(new MyAdapter());

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int temp = getIntent().getIntExtra("sign", -1);
                switch (temp){
                    case App.PHOTO_CHOOSE_SIGN_CUTIMG:
                        Intent intent = new Intent();
                        intent.setAction("com.android.camera.action.CROP");
                        Uri uri = Uri.fromFile(new File(photos.get(position)));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(uri, "image/*");
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 1);// 裁剪框比例
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("outputX", 150);// 输出图片大小
                        intent.putExtra("outputY", 150);
                        intent.putExtra("return-data", true);
                        ChoosePhotoActivity.this.startActivityForResult(intent, CROP_HEADIMG);
                        break;
                    case App.PHOTO_CHOOSE_SIGN_SENDIMG:
                        Intent intent1 = new Intent(ChoosePhotoActivity.this, TalkActivity.class);
                        intent1.putExtra("path", photos.get(position));
                        startActivity(intent1);
                        break;
                }
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (photos == null)
                return 0;
            return photos.size();
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
                convertView = LayoutInflater.from(ChoosePhotoActivity.this).inflate(R.layout.own_album_item, null, false);
                viewHolder = new ViewHolder();
                viewHolder.path = convertView.findViewById(R.id.path);
                viewHolder.img = convertView.findViewById(R.id.img);
                convertView.setTag(viewHolder);
            }
            else
                viewHolder = (ViewHolder) convertView.getTag();

            viewHolder.path.setText(photos.get(position));
            MyImgShow.showLocalImgSquare(ChoosePhotoActivity.this, "file://" + photos.get(position), viewHolder.img);
            return convertView;
        }

        public class ViewHolder {
            TextView path;
            ImageView img;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case CROP_HEADIMG:
                    save_crop(data, Environment.getExternalStorageDirectory() + "/tempHeadImg");
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //保存裁剪图片 头像
    private void save_crop(final Intent data, final String path){
        if (data != null) {

            // 拿到剪切数据
            Bitmap bmap = data.getParcelableExtra("data");
            try {
                FileOutputStream foutput = new FileOutputStream(path);
                bmap.compress(Bitmap.CompressFormat.PNG, 100, foutput);
                foutput.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!bmap.isRecycled()) {
                bmap.recycle();
            }

            try {
                final AVFile file = AVFile.withAbsoluteLocalPath("LeanCloud.png", path);
                loading.setVisibility(View.VISIBLE);
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        File file1 = new File(path);
                        if (file1.exists())
                            file1.delete();
                        if (e == null) {
                            new NetTaskCode(new NetTaskCodeListener() {
                                @Override
                                public void before() {
                                }

                                @Override
                                public int middle() {
                                    return AccountModel.getInstance().changeHeadImg(file.getUrl());
                                }

                                @Override
                                public void after(int code) {
                                    loading.setVisibility(View.GONE);
                                    AccountModel.getInstance().actListeners();
                                }
                            }).execute();
                        }
                        else
                        {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(ChoosePhotoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
