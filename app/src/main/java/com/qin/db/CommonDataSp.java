package com.qin.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CommonDataSp {
	SharedPreferences sp;
	Context context;

	/**
	 * 构造器，获取sharepreferrence对象；
	 * 
	 * @param context
	 */
	public CommonDataSp(Context context) {
		this.context = context.getApplicationContext();
		sp = context.getApplicationContext().getSharedPreferences("remind", Context.MODE_PRIVATE); // 私有数据
	}

	/**
	 * 保存
	 * 
	 * @param homeBgPath
	 */

	public void saveHomeBgPath(String homeBgPath) {
		Editor editor = sp.edit();// 获取编辑器
		editor.putString("homeBgPath", homeBgPath);
		editor.commit();// 提交修改
	}
	/**
	 * 保存
	 *
	 * @param isDim
	 */

	public void saveIsDim(boolean isDim) {
		Editor editor = sp.edit();// 获取编辑器
		editor.putBoolean("isDim", isDim);
		editor.commit();// 提交修改
	}

	/**
	 * 获取
	 * 
	 * @return
	 */
	public String getHomeBgPath() {
		return sp.getString("homeBgPath",null);
	}
	/**
	 * 获取
	 *
	 * @return
	 */
	public Boolean getIsDim() {
		return sp.getBoolean("isDim",false);
	}

	/**
	 * 清除
	 */
	public void clear(){
		Editor editor = sp.edit();// 获取编辑器
		editor.clear();
		editor.commit();// 提交修改
	}
}
