package com.example.rtyui.mvptalk.tool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by rtyui on 2018/4/25.
 */

public class NetVisitor {

    public static String postNormal(String path, String info)
    {
        try{
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream os = conn.getOutputStream();
            os.write(info.getBytes());
            os.flush();
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
            String str = br.readLine();
            return  str;
        }
        catch(Exception e){
            return null;
        }
    }

    public static int downloadImg(String url, String name){

        BufferedInputStream bufferedInputStream = null;
        OutputStream outputStream = null;
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.connect();

            // 文件大小
            int fileLength = connection.getContentLength();
            System.out.println("文件大小是 " + fileLength + "b");

            // 文件名
            String filePathUrl = connection.getURL().getFile();
            String fileFullName = filePathUrl.substring(filePathUrl.lastIndexOf(File.separatorChar) + 1);
            System.out.println(fileFullName);

            System.out.println("file length---->" + fileLength);

            bufferedInputStream = new BufferedInputStream(connection.getInputStream());

            File file = new File(App.LOCAL_IMG_PATH);
            if (!file.exists())
                file.mkdirs();
            outputStream = new FileOutputStream(App.LOCAL_IMG_PATH + name + fileFullName.substring(fileFullName.lastIndexOf(".")));
            int size = 0;
            int length = 0;
            byte[] buf = new byte[1024];
            while ((size = bufferedInputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, size);
                length += size;
                System.out.println("下载了-------> " + length * 100 / fileLength + "%");
            }
            return App.NET_SUCCEED;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return App.NET_FAil;
        }
        finally {
            try {
                if (bufferedInputStream != null)
                    bufferedInputStream.close();
            }catch (IOException e){

            }
            try {
                if (outputStream != null)
                    outputStream.close();
            }catch (IOException e){

            }
        }
    }
}
