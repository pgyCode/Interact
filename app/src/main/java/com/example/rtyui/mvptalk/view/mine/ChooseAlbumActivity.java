package com.example.rtyui.mvptalk.view.mine;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.tool.MyImgShow;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChooseAlbumActivity extends Activity {

    private ListView lst;
    private HashMap<String, List<String>> albums;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_choose_album);

        albums = getSystemPhotoList();
        lst = findViewById(R.id.lst);
        lst.setAdapter(new MyAdapter());

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChooseAlbumActivity.this, ChoosePhotoActivity.class);
                intent.putExtra("photos", (Serializable) albums.get(albums.keySet().toArray()[position]));
                intent.putExtra("sign", getIntent().getIntExtra("sign", -1));
                startActivity(intent);
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
            if (albums == null)
                return 0;
            return albums.size();
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
                convertView = LayoutInflater.from(ChooseAlbumActivity.this).inflate(R.layout.own_album_item, null, false);
                viewHolder = new ViewHolder();
                viewHolder.path = convertView.findViewById(R.id.path);
                viewHolder.img = convertView.findViewById(R.id.img);
                convertView.setTag(viewHolder);
            }
            else
                viewHolder = (ViewHolder) convertView.getTag();

            viewHolder.path.setText(((String)albums.keySet().toArray()[position]));
            MyImgShow.showLocalImgSquare(ChooseAlbumActivity.this, "file://" + albums.get(albums.keySet().toArray()[position]).get(0), viewHolder.img);
            return convertView;
        }

        public class ViewHolder {
            TextView path;
            ImageView img;
        }
    }


    private HashMap<String, List<String>> getSystemPhotoList()
    {
        HashMap<String, List<String>> temps = new HashMap<String, List<String>>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentResolver contentResolver = this.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null || cursor.getCount() <= 0)
            return temps; // 没有图片
        while (cursor.moveToNext())
        {
            int index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            String path = cursor.getString(index); // 文件地址
            File file = new File(path);
            if (file.exists())
            {
                String fatherPath = path.substring(0, path.lastIndexOf('/') - 1);
                if (temps.get(fatherPath) == null){
                    temps.put(fatherPath, new ArrayList<String>());
                }
                temps.get(fatherPath).add(path);
            }
        }
        return temps;
    }
}
