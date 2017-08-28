package com.qin.myinterface;

/**
 * 通用的dialog的接口，实现点击确定和取消的方法。
	 * 
	 * @author 123
	 * 
	 */
	public interface DialogCallBack {
		public void ok(Object obj);

		public void cancle(Object obj);
	}