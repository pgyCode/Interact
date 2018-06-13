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

import java.lang.ref.WeakReference;
import java.security.acl.Owner;

public class OwnerActivity extends Activity {

    private TextView txtId = null;
    private TextView txtNick = null;
    private ImageView imgHead = null;

    private OnModelChangeListener modelListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_owner);

        txtId = findViewById(R.id.txt_id);
        txtNick = findViewById(R.id.txt_nick);
        txtNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OwnerActivity.this,ChangeNickActivity.class));
            }
        });
        imgHead = findViewById(R.id.img_head);
        imgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(OwnerActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        OwnerActivity.this.requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                    }else {
                        Toast.makeText(OwnerActivity.this, "权限已申请", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(OwnerActivity.this, ChooseAlbumActivity.class);
                        intent.putExtra("sign", App.PHOTO_CHOOSE_SIGN_CUTIMG);
                        startActivity(intent);
                    }
                }
                else{
                    Intent intent = new Intent(OwnerActivity.this, ChooseAlbumActivity.class);
                    intent.putExtra("sign", App.PHOTO_CHOOSE_SIGN_CUTIMG);
                    startActivity(intent);
                }
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
        if (requestCode == 2){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "权限已申请", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OwnerActivity.this, ChooseAlbumActivity.class);
                    intent.putExtra("sign", App.PHOTO_CHOOSE_SIGN_CUTIMG);
                    startActivity(intent);
                }else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("权限申请").setMessage("为了能够设设置头像，请允许我们使用读取文件权限")
                            .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goToAppSetting();
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    dialog.show();
                }
            }
        }
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 123);
        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AccountModel.getInstance().listeners.remove(modelListener);
    }
}
