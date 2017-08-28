package com.qin.gallery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qin.Utils.StringUtils;
import com.qin.adapter.BasePhotoAdapter;
import com.qin.adapter.ViewHolder;
import com.qin.cons.IntegerCons;
import com.qin.love.R;
import com.qin.love.ShowPhotoActivity;
import com.qin.model.Time;

import java.util.ArrayList;

public class HorizontalScrollViewAdapter extends BasePhotoAdapter
{

	private Context mContext;
	private ArrayList<String> list=new ArrayList<String>();
	private ImageView ivPicture;
	private String url;

	public HorizontalScrollViewAdapter(Context context, Time time)
	{
		this.mContext = context;
		url=time.getImagePaths();
		if (!StringUtils.isBlank(url)) {
			String[] urls = url.split("\0");
			for (int i = 0; i < urls.length; i++) {
				list.add(urls[i]);
			}
		}

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public String getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}



	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_time_picture, null);
		}
		ivPicture= ViewHolder.get(convertView, R.id.biv_pic);
		ivPicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it=new Intent(mContext, ShowPhotoActivity.class);
				it.putExtra("paths",url);
				it.putExtra("position",position);
				mContext.startActivity(it);
			}
		});
		try {
			showImageByLoaderCacheAll(mContext,list.get(position), ivPicture, IntegerCons.NONE);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return convertView;
	}
}
