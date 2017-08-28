package com.qin.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2015/12/30.
 */
public class MemorialDay extends BmobObject {
    private String memDate;//时间
    private String content;//内容
    private boolean isCalenda;//true公历，false农历
    private String  loversId;//情侣Id 1:2

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCalenda() {
        return isCalenda;
    }

    public void setIsCalenda(boolean isCalenda) {
        this.isCalenda = isCalenda;
    }

    public String getMemDate() {
        return memDate;
    }

    public void setMemDate(String memDate) {
        this.memDate = memDate;
    }

    public String getLoversId() {
        return loversId;
    }

    public void setLoversId(String loversId) {
        this.loversId = loversId;
    }
}