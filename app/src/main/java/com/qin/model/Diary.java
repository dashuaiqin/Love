package com.qin.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by Administrator on 2015/12/28.
 */
public class Diary extends BmobObject {
    private BmobDate sendDate;//时间
    private String content;//内容
    private String weather;//天气
    private String location;//地点
    private String userId;//用户id

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public BmobDate getSendDate() {
        return sendDate;
    }

    public void setSendDate(BmobDate sendDate) {
        this.sendDate = sendDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}
