package com.qin.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.qin.love.R;
import com.qin.model.FaceText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 表情显示适配器
 */
public class EmoteAdapter extends BaseAdapter {
	private  ImageView mIvImage;//表情
	private Context context;
	protected List<FaceText> list = new ArrayList<FaceText>();
	public EmoteAdapter(Context context, FaceText... datas){
		super();
		this.context=context;
		if (datas != null && datas.length > 0) {
			list = Arrays.asList(datas);
		}
	}
	public EmoteAdapter(Context context, List<FaceText> datas) {
		this.context=context;
		if (datas != null && datas.size() > 0) {
			list = datas;
		}
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public FaceText getItem(int position) {
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
					R.layout.item_face_text, null);
		}
		mIvImage = ViewHolder.get(convertView, R.id.v_face_text);

		FaceText faceText = (FaceText) getItem(position);
		String key = faceText.text.substring(1);
		Drawable drawable =context.getResources().getDrawable(context.getResources().getIdentifier(key, "drawable", context.getPackageName()));
		mIvImage.setImageDrawable(drawable);
		return convertView;
	}
	/**
	 * 设置数据源
	 * @param list
	 */
	public void setList(List<FaceText> list) {
		this.list = list;
		notifyDataSetChanged();
	}

}
