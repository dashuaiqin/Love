package com.qin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

import com.qin.Application.MyApplication;
import com.qin.cons.IntegerCons;
import com.qin.love.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * 滚动时不加载图片，滚动停止时才加载图片
 * 
 * @author 123
 * 
 */
public class ImageGvAdapter extends BasePhotoAdapter {
	public HashMap<String, Boolean> isSelected; // 用于保存checkbox的状态
	private Context context;
	private JSONArray js;// 数据由JSONArray传进来
	private CheckBox cbPhoto;
	private ImageView ivPhoto;

	public ImageGvAdapter(Context context, JSONArray js,
			HashMap<String, Boolean> isSelected, GridView gridview) {
		super();
		this.context = context;
		this.js = js;
		if (isSelected == null)
			initCb();// 初始化checkbox状态
		else
			this.isSelected = isSelected;
	}

	@Override
	public int getCount() {
		return js.length();
	}

	@Override
	public Object getItem(int position) {
		String path = "";
		try {
			path = js.getJSONObject(position).getString("filePath");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return path;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_gv_photo, null);
		}

	cbPhoto = (CheckBox) convertView
					.findViewById(R.id.ck_photo);
		ivPhoto = (ImageView) convertView
					.findViewById(R.id.iv_photo);
if (MyApplication.isSetIcon){
	cbPhoto.setVisibility(View.GONE);
}
		final JSONObject object;
		try {
			object = js.getJSONObject(position);
			@SuppressWarnings("unused")
			final int fileId = object.getInt("fileId");// 获取图片id
			String filePath = object.getString("filePath");// 获取图片路径
			cbPhoto.setChecked(isSelected.get(filePath));
//			ivPhoto.setTag(filePath);
			showImageByLoader(context,filePath, ivPhoto, IntegerCons.NONE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}


	/**
	 * 初始化 设置所有checkbox都为未选择
	 */
	public void initCb() {
		isSelected = new HashMap<String, Boolean>();
		for (int i = 0; i < js.length(); i++) {
			try {
				isSelected
						.put(js.getJSONObject(i).getString("filePath"), false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
