package com.qin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.qin.Application.MyApplication;
import com.qin.gallery.HorizontalScrollViewAdapter;
import com.qin.gallery.MyHorizontalScrollView;
import com.qin.love.R;
import com.qin.model.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示poi的适配器
 * Created by 秦翱 on 2016/4/13.
 */
public class PoiAdapter extends BasePhotoAdapter {
    private TextView txtName;//poi名称
    private TextView txtAddress;//地址
    private CheckBox cbChoosed;
    private ArrayList<PoiItem> list;//数据源
    private Context context;
    private int choosedPosition=0;//选中的位置

    public PoiAdapter(Context context, ArrayList<PoiItem> list) {
        super();
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public PoiItem getItem(int position) {
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
                    R.layout.item_poi, null);
        }
        txtName = ViewHolder.get(convertView, R.id.txt_name);
        txtAddress = ViewHolder.get(convertView, R.id.txt_address);
        cbChoosed = ViewHolder.get(convertView, R.id.cb_choosed);

        PoiItem item = list.get(position);
        if (item != null) {
            txtName.setText(item.getTitle());
            txtAddress.setText(item.getSnippet());
        }
        if (choosedPosition==position){
            cbChoosed.setChecked(true);
        }else{
            cbChoosed.setChecked(false);
        }
        return convertView;
    }

    /**
     * 添加数据
     * @param addList
     */
    public void addList(ArrayList<PoiItem> addList) {
        if (list != null) {
            list.addAll(addList);
        } else {
            this.list = addList;
        }
        notifyDataSetChanged();
    }

    /**
     * 设置选中位置
     * @param cp
     */
    public void setChoosedPosition(int cp){
        choosedPosition=cp;
        notifyDataSetChanged();
    }

    /**
     * 获取选中的item
     */
    public PoiItem getChoosedItem(){
        return  list.get(choosedPosition);
    }
}
