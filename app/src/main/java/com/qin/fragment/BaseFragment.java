package com.qin.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qin.cons.IntegerCons;


/**
 * fragment基类
 */
public abstract class BaseFragment extends Fragment {




	/**
	 * 跳转到Activity
	 * 
	 * @param cls
	 */
	public void startActivity(Class<?> cls) {
		Intent intent = new Intent(getActivity(), cls);
		getActivity().startActivity(intent);
	}

	/**
	 * 通过imageloader显示图片
	 */
	public void showImageByLoader(String imageUrl, final ImageView imageView,int loading) {
		DrawableTypeRequest<String> dt= Glide.with(this).load(imageUrl);
		if (loading!= IntegerCons.NONE){
			dt.placeholder(loading);
		}
		dt.diskCacheStrategy(DiskCacheStrategy.ALL);
		dt.into(imageView);
	}

}
