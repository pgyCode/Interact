package com.example.rtyui.mvptalk.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rtyui on 2018/4/25.
 */

public class NetVisitor {

    public static String postNormal(String path, String Info)
    {
        String str = null;
        try{
            //1, 得到URL对象
            URL url = new URL(path);
            //2, 打开连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            //3, 设置提交类型
            conn.setRequestMethod("POST");
            //4, 设置允许写出数据,默认是不允许 false
            conn.setDoOutput(true);
            conn.setDoInput(true);//当前的连接可以从服务器读取内容, 默认是true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.connect();
            //5, 获取向服务器写出数据的流
            OutputStream os = conn.getOutputStream();
            //参数是键值队  , 不以"?"开始
            os.write(Info.getBytes());
            os.flush();
            //6, 获取响应的数据
            //得到服务器写回的响应数据
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
            str = br.readLine();
        }catch(Exception e){
            return null;
        }
        return  str;
    }
}
