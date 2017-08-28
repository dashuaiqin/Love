package com.qin.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qin.Utils.StringUtils;
import com.qin.Utils.TimeUtils;
import com.qin.love.R;
import com.qin.model.MemorialDay;
import com.qin.wheelview.LunarCalendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/12/28.
 */
public class MemAdapter extends BaseAdapter {
    private TextView txtContent;//内容
    private TextView txtDate;//日期
    private TextView txtDay;//时间
    private TextView txtState;//状态
    private List<MemorialDay> list;//数据源
    private Context context;
    private View view;//竖线，最后一行不显示

    public MemAdapter(Context context,List<MemorialDay> list){
        super();
        this.list=list;
        this.context=context;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public MemorialDay getItem(int position) {
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
                    R.layout.item_mem, null);
        }
        txtContent = ViewHolder.get(convertView, R.id.txt_content);
        txtDate=ViewHolder.get(convertView, R.id.txt_date);
        txtDay=ViewHolder.get(convertView,R.id.txt_day);
        txtState=ViewHolder.get(convertView,R.id.txt_state);
        view=ViewHolder.get(convertView,R.id.view_2);
        if (getItemViewType(position)==1){
            view.setVisibility(View.INVISIBLE);
        }
        MemorialDay mem=list.get(position);
        if (mem!=null) {
            txtContent.setText(mem.getContent());
            String dt=mem.getMemDate();
            if (!StringUtils.isBlank(dt)) {
                long mistiming=0;//时间差
                String[] date =dt.split("%");
                if (mem.isCalenda()) {
                    dt = date[0]+"-"+date[1]+"-"+date[2];
                    mistiming=getMistiming(dt);
                } else {
                    int[] solar=LunarCalendar.lunarToSolar(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(date[3]) == 1 ? true : false);
                    mistiming=getMistiming(solar[0]+"-"+solar[1]+"-"+solar[2]);//先转成公历，再求时间差
                    dt= LunarCalendar.lunartoChineseText(Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]),Integer.parseInt(date[3]));
                }
                txtDate.setText(dt);
                if (mistiming<0){
                    txtState.setText("已过");
                    txtDay.setText(""+Math.abs(mistiming));
                }else if(mistiming==0){
                    txtState.setText(null);
                    txtDay.setText("今");
                }else{
                    txtState.setText("还有");
                    txtDay.setText(""+mistiming);
                }

            }
        }
        return convertView;
    }
    /**
     * 设置数据源
     * @param list
     */
    public void setList(List<MemorialDay> list) {
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

    /**
     * 获取时间差
     * @param date
     * @return
     */
    private long getMistiming(String date){
        long days = 0;


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            Date myDt=df.parse(date+" 00:00:00");
            Date now=df.parse(TimeUtils.getDate()+" 00:00:00");
            long diff = myDt.getTime() - now.getTime();//这样得到的差值是微s级别
            days = diff / (1000 * 60 * 60 * 24);
        }
        catch (Exception e)
        {
            Log.i("HH","求时间差："+e.getMessage());
        }
        return days;
    }
}
