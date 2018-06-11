package com.example.rtyui.mvptalk.tool;

import android.content.Context;
import android.os.Environment;

import com.example.rtyui.mvptalk.bean.UserBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by rtyui on 2018/4/25.
 */

public class MyLocalObject {

    //保存数据
    public static void saveObject(String name, Object o){
        ObjectOutputStream fos=null;
        try {
            String path = App.context.getFilesDir() + "/" + name;
            System.out.println(path);
            File file=new File(path);
            fos=new ObjectOutputStream(new FileOutputStream(file));
            fos.writeObject(o);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (fos!=null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }
    }
    //保存数据
    public static Object getObject(String name){
        ObjectInputStream inputStream=null;
        try {
            String path =  App.context.getFilesDir() + "/" + name;
            File file=new File(path);
            inputStream = new ObjectInputStream(new FileInputStream(file));
            return inputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            }
        }return null;
    }

    //保存数据
    public static void delObject(String name){
        try {
            String path =  App.context.getFilesDir() + "/" + name;
            File file=new File(path);
            if (file.exists())
                file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
