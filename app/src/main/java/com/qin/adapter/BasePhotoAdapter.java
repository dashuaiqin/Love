package com.qin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qin.cons.IntegerCons;

/**
 * 父类，其中放了一些公用的方法
 *
 * @author 123
 *
 */
public class BasePhotoAdapter extends BaseAdapter {


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
     /**
	 * 通过imageloader显示图片
	 */
	public void showImageByLoader(Context context,String imageUrl, final ImageView imageView,int loading) {
		DrawableTypeRequest<String> dt= Glide.with(context).load(imageUrl);
		if (loading!= IntegerCons.NONE){
			dt.placeholder(loading);
		}
		dt.into(imageView);
		}
	/**
	 * 通过imageloader显示图片，缓存全尺寸的图片
	 */
	public void showImageByLoaderCacheAll(Context context,String imageUrl, final ImageView imageView,int loading) {
		DrawableTypeRequest<String> dt= Glide.with(context).load(imageUrl);
		if (loading!= IntegerCons.NONE){
			dt.placeholder(loading);
		}
		dt.diskCacheStrategy(DiskCacheStrategy.ALL);
		dt.into(imageView);
		}

}
