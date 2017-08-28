package com.qin.Application;

import android.app.Activity;
import android.app.Application;

import com.qin.Utils.FileUtils;
import com.qin.model.MyUser;
import com.qin.receiver.MessageHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobUser;


public class MyApplication extends Application {
	public static boolean isSetIcon=true;//是否是设置头像
	public static boolean isHomeBg;//是否是设置首页背景
	private List<Activity> activityList = new LinkedList<Activity>();// 存放Activity,用于退出
	private static MyApplication mApplication;
	public static int mScreenWidth;// 屏幕宽
	public static int mScreenHeight;// 屏幕高
	private MyUser myUser;//本地缓存,本人
	private MyUser myFriends;//另一半
	private String loversCommDataId;//共有数据的id

	public MyUser getMyUser() {
		return myUser;
	}

	public MyUser getMyFriends() {
		return myFriends;
	}

	public void setMyFriends(MyUser myFriends) {
		this.myFriends = myFriends;
	}

	public void setMyUser(MyUser myUser) {
		this.myUser = myUser;
	}

	public String getLoversCommDataId() {
		return loversCommDataId;
	}

	public void setLoversCommDataId(String loversCommDataId) {
		this.loversCommDataId = loversCommDataId;
	}

	/**
	 * SDK初始化建议放在启动页
	 */
	public static String APPID = "7ebb3702499aa5790010cdde4d090aa6";
	@Override
	public void onCreate() {
		super.onCreate();
		//只有主进程运行的时候才需要初始化
		if (getApplicationInfo().packageName.equals(getMyProcessName())){
			//im初始化
			BmobIM.init(this);
			//注册消息接收器
			BmobIM.registerDefaultMessageHandler(new MessageHandler(this));
		}
		//省略其他代码
		mApplication=this;
	}

	public static MyApplication getInstance() {
		return mApplication;
	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}




	// 遍历所有Activity并finish
	public void exitAll() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		clearCache();
	}

	/**
	 * 清除缓存
	 */
	public void clearCache() {
		File dirFile = this.getExternalCacheDir();
		if (dirFile.listFiles().length >=30) {// 如果缓存的文件有30个
			FileUtils.delete(dirFile);
		}
	}

	/**
	 * 获取当前运行的进程名
	 * @return
	 */
	public static String getMyProcessName() {
		try {
			File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
			BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
			String processName = mBufferedReader.readLine().trim();
			mBufferedReader.close();
			return processName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
