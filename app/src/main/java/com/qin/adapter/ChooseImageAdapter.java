package com.qin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qin.Application.MyApplication;
import com.qin.cons.IntegerCons;
import com.qin.love.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChooseImageAdapter extends BasePhotoAdapter {
	private Context context;
	private JSONArray js;// 数据由JSONArray传进来
	private TextView txtFileName;
	private TextView txtFileSize;
	private TextView txtChooseNum;
	private ImageView ivFile;


	public ChooseImageAdapter(Context context, JSONArray js) {
		super();
		this.context = context;
		this.js = js;

	}

	@Override
	public int getCount() {
		return js.length();
	}

	@Override
	public Object getItem(int position) {
		return position;
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
					R.layout.item_choose_img, null);
		}
		txtFileSize = ViewHolder.get(convertView,R.id.txt_file_size);
		txtChooseNum = ViewHolder.get(convertView,R.id.txt_choose_num);
		txtFileName = ViewHolder.get(convertView,R.id.txt_file_name);
		ivFile = ViewHolder.get(convertView,R.id.iv_file);
		final JSONObject object;
		try {
			object = js.getJSONObject(position);
//			int folderId = object.getInt("folderId");// 获取到文件夹id
			String folderName = object.getString("folderName");// 获取到文件夹名字
			final int fileId = object.getInt("fileId");// 获取图片id
			int fileCount = object.getInt("fileCount");// 获取到图片数量
			txtFileName.setText(folderName);
			txtFileSize.setText("共" + fileCount + "张");
			if (object.has("choosedNum")) {
				int choosedNum = object.getInt("choosedNum");
				if (choosedNum == 0|| MyApplication.isSetIcon) {
					txtChooseNum.setVisibility(View.GONE);
				} else {
					txtChooseNum.setVisibility(View.VISIBLE);
					txtChooseNum.setText("已选" + choosedNum + "张");
				}
			}
			showImageByLoader(context,object.getString("filePath"),ivFile, IntegerCons.NONE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}


}
