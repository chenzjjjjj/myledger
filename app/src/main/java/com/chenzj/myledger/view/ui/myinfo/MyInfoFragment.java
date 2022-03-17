package com.chenzj.myledger.view.ui.myinfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chenzj.myledger.dao.CacheDao;
import com.chenzj.myledger.R;
import com.chenzj.myledger.common.Constant;
import com.chenzj.myledger.model.User;
import com.chenzj.myledger.view.LoginActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MyInfoFragment extends Fragment {

    private MyInfoViewModel myInfoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my, container, false);
        ImageView blurImageView = root.findViewById(R.id.h_back);
        ImageView avatarImageView = root.findViewById(R.id.h_head);
        //背景图片虚化
        Glide.with(this).load(R.drawable.def_head)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25)))
                .into(blurImageView);

        //图片切割为圆形
        Glide.with(this).load(R.drawable.def_head)
                .apply(RequestOptions.bitmapTransform(new CropCircleTransformation()))
                .into(avatarImageView);

        myInfoViewModel = new ViewModelProvider(getActivity()).get(MyInfoViewModel.class);
        final TextView textUserName = root.findViewById(R.id.user_name);
        final TextView textUserAcc = root.findViewById(R.id.user_account);
//        User user = myInfoViewModel.getUserMLData().getValue();
//        textUserName.setText(user.getUsername());
//        textUserAcc.setText(user.getAccount());
        myInfoViewModel.getUserMLData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                textUserName.setText(user.getUsername());
                textUserAcc.setText(user.getAccount());
            }
        });

        ImageView morebt = root.findViewById(R.id.ic_exit);
        morebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialog();
            }
        });

        return root;
    }

    private void initDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottom_dialog);
        //给布局设置透明背景色，让图片突出来
        bottomSheetDialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet)
                .setBackgroundColor(getResources().getColor(android.R.color.transparent));

        bottomSheetDialog.findViewById(R.id.tv_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CacheDao cacheDao = new CacheDao(getActivity());
                cacheDao.remove(Constant.CURRENT_USER); //删除当前用户
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        bottomSheetDialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });
        bottomSheetDialog.show();
    }
}