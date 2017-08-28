package com.qin.fragment;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.qin.Application.MyApplication;
import com.qin.cons.StringCons;
import com.qin.db.CommonDataSp;
import com.qin.love.LoginActivity;
import com.qin.love.R;
import com.qin.love.UserDataActivity;
import com.qin.model.Dim;
import com.qin.model.MyUser;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2015/12/9.
 */
public class CenterFragment extends BaseFragment {
    private ImageView ivIcon;
    private View view = null;
    private TextView txtTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_center, container,
                    false);
            initView();
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;

    }

    private void initView() {
        txtTitle = (TextView) view.findViewById(R.id.txt_title);
        txtTitle.setText(StringCons.TITLE_CENTER);
        ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        ivIcon.setOnClickListener(listener);

        final SwitchButton sbDim = (SwitchButton) view.findViewById(R.id.sb_dim);
//        sbDim.setBackColorRes(R.color.white);
//        sbDim.setTintColor(Color.GREEN);
        sbDim.setThumbColorRes(R.color.title_bg);
        sbDim.setThumbSize(70, 45);
        sbDim.setFadeBack(true);
        sbDim.setChecked(new CommonDataSp(getActivity()).getIsDim());
        sbDim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new CommonDataSp(getActivity()).saveIsDim(isChecked);
                EventBus.getDefault().post(new Dim());
            }
        });
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_icon:
                    if (MyApplication.getInstance().getMyUser() != null) {//已登录
                        startActivity(UserDataActivity.class);
                    } else {//未登录
                        startActivity(LoginActivity.class);
                    }
//                  startActivity(CutPicActivity.class);
                    break;

                default:
                    break;
            }

        }
    };


    @Override
    public void onResume() {
        super.onResume();
        MyUser user = MyApplication.getInstance().getMyUser();
        if (user != null) {
            if (user.getAvatar() != null) {
                showImageByLoader(user.getAvatar(), ivIcon, R.drawable.default_avatar);
            }
        } else {
            ivIcon.setImageResource(R.drawable.default_avatar);
        }
    }


}
