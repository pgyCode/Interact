package com.example.rtyui.mvptalk.tool.dialoger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import com.example.rtyui.mvptalk.R;


public class AlertDialoger extends AlertDialog.Builder {

    public AlertDialoger(@NonNull Context context, String title, String message, final OnDialogChooseListener listener) {
        super(context);

        setTitle(title);
        setMessage(message);
        setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onPositive();
            }
        });
        setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onNegative();
            }
        });
    }
}
