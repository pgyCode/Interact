package com.example.rtyui.mvptalk.view.own;

import android.accounts.Account;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rtyui.mvptalk.Msg.Msg;
import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.MyImgShow;
import com.example.rtyui.mvptalk.tool.MySqliteHelper;
import com.example.rtyui.mvptalk.view.common.FileChooseActivity;
import com.example.rtyui.mvptalk.view.mine.OwnerActivity;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by rtyui on 2018/3/31.
 */

public class LeafFragment extends Fragment {

    private View root;


    private TextView txtId = null;
    private TextView txtNick = null;
    private ImageView imgHead = null;


    private OnModelChangeListener modelListener = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.leaf, null);

        root.findViewById(R.id.btn_owner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LeafFragment.this.getContext(), OwnerActivity.class));
            }
        });



        txtId = root.findViewById(R.id.txt_id);
        txtNick = root.findViewById(R.id.txt_nick);
        imgHead = root.findViewById(R.id.img_head);


        root.findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeafFragment.this.getActivity().finish();
            }
        });

        root.findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeafFragment.this.getActivity().finish();
                AccountModel.getInstance().clearAccountLocal();
                AccountModel.dstroyInstance();
                FriendModel.dstroyInstance();
                MsgModel.dstroyInstance();
                RequestModel.dstroyInstance();
                MySqliteHelper.dstroyInstance();
            }
        });

        root.findViewById(R.id.btn_folder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeafFragment.this.getContext(), FileChooseActivity.class);
                intent.putExtra("path", Environment.getExternalStorageDirectory().getPath() + "/interact/");
                startActivity(intent);
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
                MyImgShow.showNetImgCircle(LeafFragment.this, AccountModel.getInstance().currentUser.headImgUrl, imgHead);
            }
        };

        AccountModel.getInstance().listeners.add(modelListener);
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AccountModel.getInstance().listeners.remove(modelListener);
    }
}
