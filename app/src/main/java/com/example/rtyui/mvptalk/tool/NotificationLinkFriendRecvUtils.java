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

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.bean.AddFriendBean;
import com.example.rtyui.mvptalk.view.friend.NewFriendActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by rtyui on 2018/4/7.
 */


public class NotificationLinkFriendRecvUtils {

    private NotificationManager manager;
    public static final String id = "channel_3";
    public static final String name = "channel_name_3";

    private Context context = null;
    public NotificationLinkFriendRecvUtils(Context context){
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
    public Notification.Builder getChannelNotification(AddFriendBean bean){
        Intent intent = new Intent(context, NewFriendActivity.class);

        return new Notification.Builder(context.getApplicationContext(), id)
                .setContentTitle("好友请求回复")
                .setContentText(bean.sendNickname + " 同意添加你为好友")
                .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, 0));
    }
    public NotificationCompat.Builder getNotification_25(AddFriendBean bean){
        Intent intent = new Intent(context, NewFriendActivity.class);
        return new NotificationCompat.Builder(context.getApplicationContext())
                .setContentTitle("好友请求回复")
                .setContentText(bean.sendNickname + " 同意添加你为好友")
                .setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, 0));
    }
    public void sendNotification(AddFriendBean bean){
        if (Build.VERSION.SDK_INT>=26){
            createNotificationChannel();
            Notification notification = getChannelNotification
                    (bean).build();
            getManager().notify(3,notification);
        }else{
            Notification notification = getNotification_25(bean).build();
            getManager().notify(3,notification);
        }
    }
}