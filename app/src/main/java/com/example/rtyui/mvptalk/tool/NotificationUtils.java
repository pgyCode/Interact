package com.example.rtyui.mvptalk.tool;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.example.rtyui.mvptalk.bean.ChatBean;
import com.example.rtyui.mvptalk.view.msg.TalkActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by rtyui on 2018/4/7.
 */


public class NotificationUtils {

    private NotificationManager manager;
    public static final String id = "channel_1";
    public static final String name = "channel_name_1";

    private Context context = null;
    public NotificationUtils(Context context){
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel(){
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }
    private NotificationManager getManager(){
        if (manager == null){
            manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotification(ChatBean chatBean){
        Intent intent = new Intent(context, TalkActivity.class);
        intent.putExtra("userId", chatBean.getSendId());
        intent.putExtra("nickname", chatBean.getSendNickname());
        intent.putExtra("headImgUrl", chatBean.getSendHeadImgUrl());

        return new Notification.Builder(context.getApplicationContext(), id)
                .setContentTitle(chatBean.getSendNickname())
                .setContentText(chatBean.getMsg())
                .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, 0));
    }
    public NotificationCompat.Builder getNotification_25(ChatBean chatBean){
        Intent intent = new Intent(context, TalkActivity.class);
        System.out.println("现在的放入id " + chatBean.getSendId());
        intent.putExtra("userId", chatBean.getSendId());
        System.out.println("现在的放入----id " + intent.getIntExtra("id", -1));
        intent.putExtra("nickname", chatBean.getSendNickname());
        intent.putExtra("headImgUrl", chatBean.getSendHeadImgUrl());
        return new NotificationCompat.Builder(context.getApplicationContext())
                .setContentTitle(chatBean.getSendNickname())
                .setContentText(chatBean.getMsg())
                .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, 0));
    }
    public void sendNotification(ChatBean chatBean){
        if (Build.VERSION.SDK_INT>=26){
            createNotificationChannel();
            Notification notification = getChannelNotification
                    (chatBean).build();
            getManager().notify(1,notification);
        }else{
            Notification notification = getNotification_25(chatBean).build();
            getManager().notify(1,notification);
        }
    }
}