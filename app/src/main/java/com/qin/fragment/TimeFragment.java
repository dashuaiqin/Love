package com.qin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qin.Application.MyApplication;
import com.qin.Utils.LoveUtils;
import com.qin.Utils.StringUtils;
import com.qin.adapter.TimeAdapter;
import com.qin.cons.IntegerCons;
import com.qin.cons.StringCons;
import com.qin.love.AddTimeActivity;
import com.qin.love.LoginActivity;
import com.qin.love.MainActivity;
import com.qin.love.R;
import com.qin.model.Time;
import com.qin.xlistview.XListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 拾光fragment
 */

public class TimeFragment extends BaseFragment {
    private View view = null;
    private TextView txtTitle;
    private ImageView ivRight;//添加按钮
    private XListView xlvTime;
    private int skipNum;//跳过的次数
    private static int SELECTE_NUM = 10;//每次查询十个
    private ArrayList<Time> timeList;//数据
    private TimeAdapter adapter;
    private RelativeLayout rlNodata;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_time, container,
                    false);
            if (MyApplication.getInstance().getMyUser() == null) {//未登录
                ((MainActivity) getActivity()).startActivity(LoginActivity.class, false);
            } if (StringUtils.isBlank(MyApplication.getInstance().getMyUser().getMyFriend())) {//没有添加另一半

            } else {
                initView();
                refresh();
            }
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }


    /**
     * 界面初始化
     */
    private void initView() {
        txtTitle = (TextView) view.findViewById(R.id.txt_title);
        txtTitle.setText(StringCons.TITLE_TIME);
        ivRight = (ImageView) view.findViewById(R.id.iv_right);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.drawable.add);
        ivRight.setOnClickListener(listener);
        rlNodata= (RelativeLayout) view.findViewById(R.id.rl_no_data);
        rlNodata.setOnClickListener(listener);

        xlvTime = (XListView) view.findViewById(R.id.xlv_time);
        xlvTime.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                getData();
            }
        });
        ((MainActivity) getActivity()).startProgressDialog();
    }

    /**
     * 控件的监听
     */
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_right:
//                    startActivity(AddTimeActivity.class);
                    Intent intent = new Intent(getActivity(), AddTimeActivity.class);
                    getActivity().startActivityForResult(intent, IntegerCons.TYPE_ADD_TIME);
                    break;
                case R.id.rl_no_data:
                    rlNodata.setVisibility(View.GONE);
                    xlvTime.setVisibility(View.VISIBLE);
                    refresh();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 刷新
     */
    public void refresh() {
        if (MyApplication.getInstance().getMyUser() == null|| StringUtils.isBlank(MyApplication.getInstance().getMyUser().getMyFriend())) {//未登录
            return;
        }
        timeList = new ArrayList<Time>();
        skipNum = 0;
        getData();
    }

    /**
     * 从服务器拉数据
     */
    private void getData() {
        if (MyApplication.getInstance().getMyUser() == null|| StringUtils.isBlank(MyApplication.getInstance().getMyUser().getMyFriend())) {//未登录或未添加另一半
            return;
        }
        BmobQuery<Time> query = new BmobQuery<Time>();
        //查询
        query.addWhereEqualTo("loversId", LoveUtils.getLoversId());
        //返回10条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(SELECTE_NUM);
        query.setSkip(skipNum);
        query.order("-updatedAt");
        //执行查询方法
        query.findObjects(new FindListener<Time>() {
            @Override
            public void done(List<Time> list, BmobException e) {
                if(e==null){
                    timeList.addAll(list);
                    xlvTime.stopRefresh();
                    xlvTime.stopLoadMore();
                    skipNum += SELECTE_NUM;
                    if (list.size() < SELECTE_NUM) {//已经查完了
                        xlvTime.setPullLoadEnable(false);
                    } else {
                        xlvTime.setPullLoadEnable(true);
                    }
                    showData();
                    ((MainActivity) getActivity()).stopProgressDialog();
                }else{
                    if (getActivity() == null) {
                        return;
                    }
                    ((MainActivity) getActivity()).shortToast("查询失败：" + e.getMessage());
                    xlvTime.stopRefresh();
                    xlvTime.stopLoadMore();
                    ((MainActivity) getActivity()).stopProgressDialog();
                    if (adapter == null) {
                        rlNodata.setVisibility(View.VISIBLE);
                        xlvTime.setVisibility(View.GONE);
                    }
                }

            }
        });
    }

    /**
     * 显示数据
     */
    private void showData() {
        if (adapter == null&&timeList.size()>0) {
            adapter = new TimeAdapter(getActivity(), timeList);
            xlvTime.setAdapter(adapter);
        } else {
            adapter.setList(timeList);
        }
    }

    @Override
    public void onResume() {
//        refresh();
        super.onResume();
    }
}
