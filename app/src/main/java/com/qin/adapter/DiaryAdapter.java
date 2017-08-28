package com.qin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qin.Utils.StringUtils;
import com.qin.love.R;
import com.qin.model.Diary;

import java.util.List;

/**
 * Created by Administrator on 2015/12/28.
 */
public class DiaryAdapter extends BaseAdapter {
    private TextView txtContent;//内容
    private TextView txtDate;//日期
    private TextView txtTime;//时间
    private List<Diary> list;//数据源
    private Context context;
    private View view;//竖线，最后一行不显示

    public DiaryAdapter(Context context,List<Diary> list){
        super();
        this.list=list;
        this.context=context;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Diary getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        super.getView( position,  convertView,  parent);
//        Log.i("HH", "内容：" + list.size());?
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_diary, null);
        }
        txtContent = ViewHolder.get(convertView, R.id.txt_content);
        txtDate=ViewHolder.get(convertView, R.id.txt_date);
        txtTime=ViewHolder.get(convertView,R.id.txt_time);
        view=ViewHolder.get(convertView,R.id.view_2);
        if (getItemViewType(position)==1){
            view.setVisibility(View.INVISIBLE);
        }
        Diary diary=list.get(position);
        if (diary!=null) {
            txtContent.setText(diary.getContent());
            String dt=diary.getSendDate().getDate();
            if (!StringUtils.isBlank(dt)){
            String[] time=dt.split(" ");
            txtDate.setText(time[0]);
            txtTime.setText(time[1]);
            }
        }
        return convertView;
    }
    /**
     * 设置数据源
     * @param list
     */
    public void setList(List<Diary> list) {
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
        if (position == getCount() - 1) {
            result = 1;
        }
        return result;
    }
}
