package com.qin.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/1/19.
 */
public class Time extends BmobObject {
    private String content;//内容
    private String loversId;//情侣Id 1:2
    private String location;//地点
    private String fromId;//发送人id
    private String imagePaths;//图片的路径些

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(String imagePaths) {
        this.imagePaths = imagePaths;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLoversId() {
        return loversId;
    }

    public void setLoversId(String loversId) {
        this.loversId = loversId;
    }
}