package com.example.rtyui.mvptalk.view.own;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rtyui.mvptalk.R;
import com.example.rtyui.mvptalk.model.AccountModel;
import com.example.rtyui.mvptalk.model.FriendModel;
import com.example.rtyui.mvptalk.model.MsgModel;
import com.example.rtyui.mvptalk.model.RequestModel;
import com.example.rtyui.mvptalk.model.TeamModel;
import com.example.rtyui.mvptalk.model.TeamRequestModel;
import com.example.rtyui.mvptalk.model.TempUserModel;
import com.example.rtyui.mvptalk.parent.OnModelChangeListener;
import com.example.rtyui.mvptalk.tool.MyImgShow;
import com.example.rtyui.mvptalk.tool.NetTaskCode;
import com.example.rtyui.mvptalk.tool.NetTaskCodeListener;
import com.example.rtyui.mvptalk.tool.permission.OnAskAppearListener;
import com.example.rtyui.mvptalk.tool.permission.PermissionAsker;
import com.example.rtyui.mvptalk.view.common.FileChooseActivity;
import com.example.rtyui.mvptalk.view.friend.NewFriendActivity;
import com.example.rtyui.mvptalk.view.mine.OwnerActivity;
import com.example.rtyui.mvptalk.view.team.AskerActivity;
import com.example.rtyui.mvptalk.view.team.TeamCreateActivity;

/**
 * Created by rtyui on 2018/3/31.
 */

public class LeafFragment extends Fragment {

    private View root;


    private TextView txtId = null;
    private TextView txtNick = null;
    private ImageView imgHead = null;

    private TextView txtWarn = null;
    private TextView txtTeamWarn = null;

    private OnModelChangeListener modelListener = null;


    private PermissionAsker fileAsker = null;
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


        fileAsker = new PermissionAsker(this, new OnAskAppearListener() {
            @Override
            public void onAppear() {
                Intent intent = new Intent(LeafFragment.this.getContext(), FileChooseActivity.class);
                intent.putExtra("path", Environment.getExternalStorageDirectory().getPath() + "/interact/");
                startActivity(intent);
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, 3, 3 ,"为了查看文件，我希望获取读取文件权限", true);

        txtId = root.findViewById(R.id.txt_id);
        txtNick = root.findViewById(R.id.txt_nick);
        imgHead = root.findViewById(R.id.img_head);
        txtWarn = root.findViewById(R.id.txt_warn);
        txtTeamWarn = root.findViewById(R.id.txt_teamwarn);

        root.findViewById(R.id.btn_warn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LeafFragment.this.getContext(), NewFriendActivity.class));
            }
        });

        root.findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeafFragment.this.getActivity().finish();
                System.exit(0);
            }
        });

        root.findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeafFragment.this.getActivity().finish();
                AccountModel.getInstance().clearAccountLocal();
                System.exit(0);
            }
        });


        root.findViewById(R.id.btn_create_team).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LeafFragment.this.getContext(), TeamCreateActivity.class));
            }
        });
        root.findViewById(R.id.btn_teamwarn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LeafFragment.this.getContext(), AskerActivity.class));
            }
        });

        root.findViewById(R.id.btn_folder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileAsker.onAsk();
            }
        });


        txtId.setText(AccountModel.getInstance().currentUser.id + "");
        txtNick.setText(AccountModel.getInstance().currentUser.nickname + "");
        MyImgShow.showNetImgCircle(this, AccountModel.getInstance().currentUser.headImgUrl, imgHead);
        txtWarn.setVisibility(View.GONE);
        txtTeamWarn.setVisibility(View.GONE);

        modelListener = new OnModelChangeListener() {
            @Override
            public void onChange() {
                txtId.setText(AccountModel.getInstance().currentUser.id + "");
                txtNick.setText(AccountModel.getInstance().currentUser.nickname + "");
                MyImgShow.showNetImgCircle(LeafFragment.this, AccountModel.getInstance().currentUser.headImgUrl, imgHead);
                if (RequestModel.getInstance().isWarn()){
                    txtWarn.setVisibility(View.VISIBLE);
                }else{
                    txtWarn.setVisibility(View.GONE);
                }
                if (TeamRequestModel.getInstance().isWarn()){
                    txtTeamWarn.setVisibility(View.VISIBLE);
                }else{
                    txtTeamWarn.setVisibility(View.GONE);
                }
            }
        };

        AccountModel.getInstance().listeners.add(modelListener);
        RequestModel.getInstance().listeners.add(modelListener);
        TeamRequestModel.getInstance().listeners.add(modelListener);
        return root;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        fileAsker.onChoose(requestCode, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fileAsker.onSet(requestCode);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AccountModel.getInstance().listeners.remove(modelListener);
        RequestModel.getInstance().listeners.remove(modelListener);
        TeamRequestModel.getInstance().listeners.remove(modelListener);
    }
}
