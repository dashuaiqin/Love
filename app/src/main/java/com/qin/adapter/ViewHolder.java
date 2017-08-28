package com.qin.adapter;

import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}

	public static TextView setTextView(View view, int id, String text) {
		TextView tv = get(view, id);
		tv.setText(text);
		return tv;
	}

	public static TextView getTextView(View view, int id) {
		TextView tv = get(view, id);
		return tv;
	}

	public static Button setButton(View view, int id, String text) {
		Button bt = get(view, id);
		bt.setText(text);
		return bt;
	}

	public static Button getButton(View view, int id) {
		Button bt = get(view, id);
		return bt;
	}

	public static CheckBox setCheckBox(View view, int id, boolean isCheck) {
		CheckBox cb = get(view, id);
		cb.setChecked(isCheck);
		return cb;
	}

	public static CheckBox getCheckBox(View view, int id) {
		CheckBox cb = get(view, id);
		return cb;
	}

	public static ImageView getImageView(View view, int id) {
		ImageView imageView = get(view, id);
		return imageView;
	}

	public static ImageView setImageResource(View view, int id, int drawable) {
		ImageView imageView = get(view, id);
		imageView.setImageResource(drawable);
		return imageView;
	}

	public static ImageView setBackgroundResource(View view, int id,
			int drawable) {
		ImageView imageView = get(view, id);
		imageView.setBackgroundResource(drawable);
		return imageView;
	}

}
