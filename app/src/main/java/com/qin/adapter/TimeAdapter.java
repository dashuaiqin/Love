package com.qin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qin.Application.MyApplication;
import com.qin.gallery.HorizontalScrollViewAdapter;
import com.qin.gallery.MyHorizontalScrollView;
import com.qin.love.R;
import com.qin.model.Time;

import java.util.List;

/**
 * 拾光的适配器
 * Created by Administrator on 2016/1/22.
 */
public class TimeAdapter extends BasePhotoAdapter {
    private ImageView ivAvatar;//头像
    private TextView txtContent;//内容
    private TextView txtDate;//日期
    private TextView txtNick;//昵称
    private TextView txtLocation;//地点
    private MyHorizontalScrollView hsvPhoto;//Gallery
    private List<Time> list;//数据源
    private Context context;

    public TimeAdapter(Context context,List<Time> list){
        super();
        this.list=list;
        this.context=context;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Time getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_time, null);
        }
        ivAvatar=ViewHolder.get(convertView,R.id.iv_avatar);

        txtContent = ViewHolder.get(convertView, R.id.txt_content);
        txtDate=ViewHolder.get(convertView, R.id.txt_date);
        txtNick=ViewHolder.get(convertView,R.id.txt_nick);
        txtLocation=ViewHolder.get(convertView,R.id.txt_location);
        hsvPhoto=ViewHolder.get(convertView,R.id.hsv_photo);
        Time time=list.get(position);
        if (time!=null) {
//            hsvPhoto.setTag(time.getObjectId());
//            ivAvatar.setTag(time.getFromId());
            txtContent.setText(time.getContent());
            if ("".equals(time.getContent())){
                txtContent.setVisibility(View.GONE);
            }else{
                txtContent.setVisibility(View.VISIBLE);
            }
            txtDate.setText(time.getUpdatedAt());
            if ("".equals(time.getLocation())){
                txtLocation.setVisibility(View.GONE);
            }else{
                txtLocation.setVisibility(View.VISIBLE);
                txtLocation.setText(time.getLocation());
            }
            if (getItemViewType(position)==1){
                showImageByLoaderCacheAll(context, MyApplication.getInstance().getMyUser().getAvatar(), ivAvatar, R.drawable.default_avatar);
                txtNick.setText(MyApplication.getInstance().getMyUser().getNick());
            }else{
                showImageByLoaderCacheAll(context,MyApplication.getInstance().getMyFriends().getAvatar(),ivAvatar,R.drawable.default_avatar);
                txtNick.setText(MyApplication.getInstance().getMyFriends().getNick());
            }

                hsvPhoto.initDatas(new HorizontalScrollViewAdapter(context,time));
        }
        return convertView;
    }
    /**
     * 设置数据源
     * @param list
     */
    public void setList(List<Time> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        int result = 0;
        Time time=list.get(position);
        if (time!=null&&MyApplication.getInstance().getMyUser()!=null&&MyApplication.getInstance().getMyUser().getObjectId().equals(time.getFromId())) {
            result = 1;
        }
        return result;
    }
}
