package com.example.rtyui.mvptalk.view.common;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.view.msg.TalkActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileChooseActivity extends Activity {

    private TextView txt = null;
    private Button btn = null;
    private ListView lst = null;

    private File parentFile;
    private List<Node> nodes;

    private MyAdapter myAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_choose_file);


        txt = findViewById(R.id.txt);
        btn = findViewById(R.id.btn);
        lst = findViewById(R.id.lst);

        lst.setAdapter(myAdapter = new MyAdapter());



        nodes = new ArrayList<>();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (!parentFile.getCanonicalPath().equals(Environment.getExternalStorageDirectory().getCanonicalPath())){
                        parentFile = parentFile.getParentFile();
                        mkLst();
                    }else{
                    }
                }catch(IOException e){

                }
            }
        });


        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final String path = getIntent().getStringExtra("path");

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (nodes.get(position).isDir){
                    parentFile = new File(nodes.get(position).path);
                    mkLst();
                } else {
                    if (path == null)
                        chooseFile(nodes.get(position).path);
                    else
                        openFile(nodes.get(position).path);
                }
            }
        });

        if (path == null)
            parentFile = Environment.getExternalStorageDirectory();
        else
            parentFile = new File(path);
        mkLst();
    }

    private void mkLst(){
        nodes.clear();
        if (parentFile.exists()){
            File[] childFiles = parentFile.listFiles();
            for (int i = 0; i < childFiles.length; i++){
                Node node = new Node();
                node.isDir = childFiles[i].isDirectory();
                node.name = childFiles[i].getName();
                node.path = childFiles[i].getAbsolutePath();
                nodes.add(node);
            }

            myAdapter.notifyDataSetChanged();
            try {
                txt.setText(parentFile.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public class Node{
        public boolean isDir;
        public String name;
        public String path;
    }

    @Override
    public void onBackPressed() {
        try{
            if (!parentFile.getCanonicalPath().equals(Environment.getExternalStorageDirectory().getCanonicalPath())){
                parentFile = parentFile.getParentFile();
                mkLst();
            }else{
                finish();
            }
        }catch(IOException e){

        }
    }


    public class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if (nodes == null)
                return 0;
            return nodes.size();
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
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(FileChooseActivity.this).inflate(R.layout.common_choose_file_item, null, false);
                viewHolder.img = convertView.findViewById(R.id.img);
                viewHolder.txt = convertView.findViewById(R.id.txt);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (nodes.get(position).isDir)
                viewHolder.img.setImageResource(R.drawable.folder);
            else
                viewHolder.img.setImageResource(R.drawable.file);
            viewHolder.txt.setText(nodes.get(position).name);
            return convertView;
        }

        public class ViewHolder{
            public TextView txt;
            public ImageView img;
        }
    }

    public void chooseFile(String path){
        Intent intent = new Intent(this, TalkActivity.class);
        intent.putExtra("choose_sign", App.CHOOSE_FILE_INTENT);
        intent.putExtra("path", path);
        startActivity(intent);
    }

    public void openFile(String path){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(new File(path));
        //设置intent的data和Type属性。
        intent.setDataAndType(Uri.fromFile(new File(path)), type);
        //跳转
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "找不到打开此文件的应用！", Toast.LENGTH_SHORT).show();
        }
    }

    private static String getMIMEType(File file) {
        String type="*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if(dotIndex < 0){
            return type;
        }
        /* 获取文件的后缀名*/
        String end=fName.substring(dotIndex,fName.length()).toLowerCase();
        if(end=="")return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if(end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }
    private static final String[][] MIME_MapTable = {
            // {后缀名，MIME类型}
            { ".3gp", "video/3gpp" },
            { ".apk", "application/vnd.android.package-archive" },
            { ".asf", "video/x-ms-asf" },
            { ".avi", "video/x-msvideo" },
            { ".bin", "application/octet-stream" },
            { ".bmp", "image/bmp" },
            { ".c", "text/plain" },
            { ".class", "application/octet-stream" },
            { ".conf", "text/plain" },
            { ".cpp", "text/plain" },
            { ".doc", "application/msword" },
            { ".docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
            { ".xls", "application/vnd.ms-excel" },
            { ".xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
            { ".exe", "application/octet-stream" },
            { ".gif", "image/gif" },
            { ".gtar", "application/x-gtar" },
            { ".gz", "application/x-gzip" },
            { ".h", "text/plain" },
            { ".htm", "text/html" },
            { ".html", "text/html" },
            { ".jar", "application/java-archive" },
            { ".java", "text/plain" },
            { ".jpeg", "image/jpeg" },
            { ".jpg", "image/jpeg" },
            { ".js", "application/x-javascript" },
            { ".log", "text/plain" },
            { ".m3u", "audio/x-mpegurl" },
            { ".m4a", "audio/mp4a-latm" },
            { ".m4b", "audio/mp4a-latm" },
            { ".m4p", "audio/mp4a-latm" },
            { ".m4u", "video/vnd.mpegurl" },
            { ".m4v", "video/x-m4v" },
            { ".mov", "video/quicktime" },
            { ".mp2", "audio/x-mpeg" },
            { ".mp3", "audio/x-mpeg" },
            { ".mp4", "video/mp4" },
            { ".mpc", "application/vnd.mpohun.certificate" },
            { ".mpe", "video/mpeg" },
            { ".mpeg", "video/mpeg" },
            { ".mpg", "video/mpeg" },
            { ".mpg4", "video/mp4" },
            { ".mpga", "audio/mpeg" },
            { ".msg", "application/vnd.ms-outlook" },
            { ".ogg", "audio/ogg" },
            { ".pdf", "application/pdf" },
            { ".png", "image/png" },
            { ".pps", "application/vnd.ms-powerpoint" },
            { ".ppt", "application/vnd.ms-powerpoint" },
            { ".pptx",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation" },
            { ".prop", "text/plain" }, { ".rc", "text/plain" },
            { ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" },
            { ".sh", "text/plain" }, { ".tar", "application/x-tar" },
            { ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
            { ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
            { ".wmv", "audio/x-ms-wmv" },
            { ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
            { ".z", "application/x-compress" },
            { ".zip", "application/x-zip-compressed" }, { "", "*/*" } };
}
