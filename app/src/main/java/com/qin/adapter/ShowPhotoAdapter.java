package com.qin.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qin.Utils.StringUtils;
import com.qin.love.R;

import java.util.ArrayList;
import java.util.HashMap;

import uk.co.senab.photoview.PhotoView;

/**
 * 放大缩小图片viewpager的适配器
 * 
 * @author Administrator
 * 
 */
public class ShowPhotoAdapter extends PagerAdapter {
	private Context context;//上下文
	private HashMap<Integer,View> mHashMap;//Hashmap保存ItemView
	private ArrayList<String> list=new ArrayList<String>();

	/**
	 * 构造器
	 * @param context
	 * @param url
	 */
	public ShowPhotoAdapter(Context context, String url) {
		this.context = context;
		mHashMap = new HashMap<Integer, View>();// 记录页面
		Log.i("HH","路径："+url);
		if (!StringUtils.isBlank(url)) {
			String[] urls = url.split("\0");
			for (int i = 0; i < urls.length; i++) {
				list.add(urls[i]);
			}
		}
	}
	@Override
	public int getCount() {
		Log.i("HH","长度："+list.size());
		return list.size();
	}


	// 这里就是初始化ViewPagerItemView.如果ViewPagerItemView已经存在,
	// 重新reload，不存在new一个并且填充数据.
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View itemView;
		if (mHashMap.containsKey(position)) {
			itemView = mHashMap.get(position);
		} else {
			itemView = LayoutInflater.from(context).inflate(
					R.layout.item_show_photo, null);
			PhotoView pvPicture= (PhotoView) itemView.findViewById(R.id.pv_picture);
			showImageByLoader(list.get(position), pvPicture, R.drawable.icon_loading);
			mHashMap.put(position, itemView);
		}
		container.addView(itemView);
		return itemView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view==object;
	}

	/**
	 * 通过imageloader显示图片
	 */
	public void showImageByLoader(String imageUrl, final ImageView imageView,int loding) {
		Glide.with(context)
				.load(imageUrl)
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.into(imageView);
	}

}
