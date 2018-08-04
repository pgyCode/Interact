package com.example.rtyui.mvptalk.view.mine;

import android.Manifest;
import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.App;
import com.example.rtyui.mvptalk.tool.MyImgShow;
import com.example.rtyui.mvptalk.tool.permission.OnAskAppearListener;
import com.example.rtyui.mvptalk.tool.permission.PermissionAsker;

import java.lang.ref.WeakReference;
import java.security.acl.Owner;

public class OwnerActivity extends Activity {

    private TextView txtId = null;
    private TextView txtNick = null;
    private ImageView imgHead = null;

    private OnModelChangeListener modelListener;

    private PermissionAsker fileAsker = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_owner);

        txtId = findViewById(R.id.txt_id);
        txtNick = findViewById(R.id.txt_nick);

        findViewById(R.id.btn_nick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OwnerActivity.this,ChangeNickActivity.class));
            }
        });
        imgHead = findViewById(R.id.img_head);

        fileAsker = new PermissionAsker(this, new OnAskAppearListener() {
            @Override
            public void onAppear() {
                Intent intent = new Intent(OwnerActivity.this, ChooseAlbumActivity.class);
                intent.putExtra("sign", App.PHOTO_CHOOSE_SIGN_CUTIMG);
                startActivity(intent);
            }
        },Manifest.permission.READ_EXTERNAL_STORAGE, 2, 2, "为了读取可用图片，请允许我们使用读取文件权限", true);

        findViewById(R.id.btn_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileAsker.onAsk();
            }
        });


        txtId.setText(AccountModel.getInstance().currentUser.id + "");
        txtNick.setText(AccountModel.getInstance().currentUser.nickname + "");
        MyImgShow.showNetImgCircle(this, AccountModel.getInstance().currentUser.headImgUrl, imgHead);

        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                txtId.setText(AccountModel.getInstance().currentUser.id + "");
                txtNick.setText(AccountModel.getInstance().currentUser.nickname + "");
                MyImgShow.showNetImgCircle(OwnerActivity.this, AccountModel.getInstance().currentUser.headImgUrl, imgHead);
            }
        };
        AccountModel.getInstance().listeners.add(modelListener);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        fileAsker.onChoose(requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fileAsker.onSet(requestCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AccountModel.getInstance().listeners.remove(modelListener);
    }
}
