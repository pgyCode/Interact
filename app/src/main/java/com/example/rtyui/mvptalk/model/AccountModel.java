package com.example.rtyui.mvptalk.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.rtyui.mvptalk.Msg.Msg;
import com.example.rtyui.mvptalk.Msg.UserMsg;
import com.example.rtyui.mvptalk.bean.UserBean;
import com.example.rtyui.mvptalk.parent.Model;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.NetVisitor;
import com.google.gson.Gson;

import java.net.URLEncoder;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by rtyui on 2018/5/5.
 */

public class AccountModel extends Model {

    public UserBean currentUser;
    public boolean onLine = false;

    private static AccountModel instance;
    private static final Object mLock = new Object();
    public static AccountModel getInstance() {
        synchronized (mLock) {
            if (instance == null)
                instance = new AccountModel();
            return instance;
        }
    }

    public static synchronized void dstroyInstance() {
        if (instance != null) instance = null;
    }


    public void init(){
        SharedPreferences sharedPreferences = App.context.getSharedPreferences("currentUser", MODE_PRIVATE);
        if (System.currentTimeMillis() - sharedPreferences.getLong("lastLogin", -1) < App.ACCOUNT_TIME){
            currentUser = new UserBean(
                    sharedPreferences.getInt("id", -1),
                    sharedPreferences.getString("nickname", ""),
                    sharedPreferences.getString("headImgUrl", ""));
        }
    }


    /**
     * 登陆
     * @param id id
     * @param password 密码
     * @return 登陆结果
     */
    public int doLogin(String id, String password) {
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/user/login", "id=" +
                    id +
                    "&password=" +
                    password);
            UserMsg msg = new Gson().fromJson(temp, UserMsg.class);
            currentUser = msg.getData();
            saveAccountLocal();
            return msg.getCode();
        }catch(Exception e){
            return -1;
        }
    }

    /**
     * 注册
     * @param nickname 昵称
     * @param password 密码
     * @return 注册结果
     */
    public int doRegist(String nickname, String password) {
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/user/regist", "nickname=" +
                    URLEncoder.encode(nickname, "UTF-8")+
                    "&password=" +
                    password);
            System.out.println("nickname=" +
                    URLEncoder.encode(nickname, "UTF-8")+
                    "&password=" +
                    password);
            System.out.println("访问的字符串" + temp);
            UserMsg msg = new Gson().fromJson(temp, UserMsg.class);
            currentUser = msg.getData();
            saveAccountLocal();
            return msg.getCode();
        }catch(Exception e){
            return -1;
        }
    }


    /**
     * 保存账户到本地
     */
    private void saveAccountLocal(){
        SharedPreferences sharedPreferences = App.context.getSharedPreferences("currentUser", MODE_PRIVATE);
        sharedPreferences.edit().
                putLong("lastLogin", System.currentTimeMillis()).
                putInt("id", currentUser.id).
                putString("headImgUrl", currentUser.headImgUrl).
                putString("nickname", currentUser.nickname).
                commit();
    }

    /**
     * 清理本地账号
     */
    public void clearAccountLocal(){
        this.currentUser = null;
        SharedPreferences sharedPreferences = App.context.getSharedPreferences("currentUser", MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }

    /**
     * 修改昵称
     * @param nick 昵称
     * @return 结果
     */
    public int changeNick(String nick) {
        try{
            String temp = NetVisitor.postNormal(App.host + "Talk/user/updateNickname", "userId=" +
                    AccountModel.getInstance().currentUser.id +
                    "&nickname=" +
                    nick);
            Msg msg = new Gson().fromJson(temp, Msg.class);
            if (msg.code == 2){
                AccountModel.getInstance().currentUser.nickname = nick;
                SharedPreferences sharedPreferences = App.context.getSharedPreferences("currentUser", MODE_PRIVATE);
                sharedPreferences.edit().
                        putString("nickname", currentUser.nickname).
                        commit();
            }
            return msg.code;
        }catch(Exception e){
            return -1;
        }
    }

    /**
     * 修改头像
     * @param headImgUrl 头像路径
     * @return 修改结果
     */
    public int changeHeadImg(String headImgUrl) {
        try {
            String temp = NetVisitor.postNormal(App.host + "Talk/user/updateHeadImgUrl", "userId=" +
                    AccountModel.getInstance().currentUser.id +
                    "&headImgUrl=" +
                    headImgUrl);
            Msg msg = new Gson().fromJson(temp, Msg.class);
            if (msg.code == 2) {
                AccountModel.getInstance().currentUser.headImgUrl = headImgUrl;
                SharedPreferences sharedPreferences = App.context.getSharedPreferences("currentUser", MODE_PRIVATE);
                sharedPreferences.edit().
                        putString("headImgUrl", currentUser.headImgUrl).
                        commit();
            }
            return msg.code;
        } catch (Exception e) {
            return -1;
        }
    }
}
