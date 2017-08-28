package com.qin.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**
 * 自定义显示照片的viewPager
 * @author Administrator
 *
 */
public class ShowPhotoViewPager extends ViewPager {
	public ShowPhotoViewPager(Context context) {
		super(context);
	}

	public ShowPhotoViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		try {
			return super.onTouchEvent(ev);
		} catch (IllegalArgumentException ex) {
		}
		return false;
	}



}