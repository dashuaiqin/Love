package com.qin.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by 秦翱 on 2017/3/29.
 */
public class LoversCommonData extends BmobObject {
    private String loversId;//情侣Id 1:2
    private String homeImgPath;//图片的路径些

    public LoversCommonData() {
    }

    public LoversCommonData(String loversId, String homeImgPath) {
        this.loversId = loversId;
        this.homeImgPath = homeImgPath;
    }

    public String getLoversId() {
        return loversId;
    }

    public void setLoversId(String loversId) {
        this.loversId = loversId;
    }

    public String getHomeImgPath() {
        return homeImgPath;
    }

    public void setHomeImgPath(String homeImgPath) {
        this.homeImgPath = homeImgPath;
    }
}
