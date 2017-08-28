package com.qin.Utils;

import com.qin.Application.MyApplication;
import com.qin.model.MyUser;

/**
 * Created by Administrator on 2016/1/7.
 */
public class LoveUtils {
    /**
     * 获取俩人共同的id
     * @return
     */
    public static String getLoversId(){
    MyUser me= MyApplication.getInstance().getMyUser();
    String loversId=me.getObjectId()+":"+me.getMyFriend();
    if (me.getMyFriend().compareTo(me.getObjectId())>0){
        loversId=me.getMyFriend()+":"+me.getObjectId();
    }
        return loversId;
    }
}
