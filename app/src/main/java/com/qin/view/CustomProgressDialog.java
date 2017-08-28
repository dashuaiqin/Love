package com.qin.view;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.qin.love.R;


/**
 * @Description:自定义对话框
 * @author http://blog.csdn.net/finddreams
 */
public class CustomProgressDialog extends ProgressDialog {
	public TextView txtExplain;

	public CustomProgressDialog(Context context) {
		super(context,R.style.NobackDialog);
		setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		setContentView(R.layout.dialog_custom_progress);
		txtExplain = (TextView) findViewById(R.id.txt_explain);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) { // TODO
	// super.onWindowFocusChanged(hasFocus);
	}
}

