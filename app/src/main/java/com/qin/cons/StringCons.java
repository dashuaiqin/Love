package com.qin.cons;

import android.os.Environment;

/**
 * Created by Administrator on 2015/11/5.
 */
public class StringCons {
    // 本应用中的照片缓存路径
    public static final String PHOTO_CACHE_DIR = Environment
            .getExternalStorageDirectory() + "/love/Cache";
    // 本应用中的照片存储路径
    public static final String PHOTO_DIR = Environment
            .getExternalStorageDirectory() + "/love/photo";

    public static String PUT_IN_NAME="请输入用户名";
    public static String PUT_IN_PASS="请输入密码";
    public static String PASS_NOT_SAME="输入密码不一致，请重输";
    public static String EXIT="再按一次退出应用";
    public static String TITLE_REGISTER="注册";
    public static String TITLE_DIARY="日记本";
    public static String TITLE_ADD_DIARY="写日记";
    public static String TITLE_READ_DIARY="查看日记";
    public static String TITLE_UPDATE_DIARY="修改日记";
    public static String TITLE_ADD_MEMORY_DAY="添加纪念日";
    public static String TITLE_UPDATE_MEMORY_DAY="编辑纪念日";
    public static String TITLE_TIME="拾光";
    public static String TITLE_CUT_PIC="图片裁剪";
    public static String TITLE_CENTER="个人中心";
    public static String TITLE_USER_DATA="个人资料";
    public static String TITLE_GET_LOCATION="位置";
    public static String FINISH="完成";
    public static String CONFIRM="确定";
}
