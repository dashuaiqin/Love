package com.qin.model;

import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobUser;


public class MyUser extends BmobUser {

    /**
     *
     */

    private static final long serialVersionUID = 1L;
    /**
     * 年龄
     */
    private Integer age;

    /**
     * 另一半
     */
    private String myFriend;
    /**
     * 头像文件的名称(由于Bmobsdk修改，此处实际存的是file.geturl)
     */
    private String avatarFileName;
    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nick;

    /**
     * //性别-true-男
     */
    private Boolean sex;
    /**
     * 地区位置代码
     */
    private String locationCode;//记录在本地数组中的位置

    private List<String> goodAt;
    private List<JSONObject> notice;

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public String getMyFriend() {
        return myFriend;
    }

    public void setMyFriend(String myFriend) {
        this.myFriend = myFriend;
    }

    public List<String> getGoodAt() {
        return goodAt;
    }

    public void setGoodAt(List<String> goodAt) {
        this.goodAt = goodAt;
    }

    public List<JSONObject> getNotice() {
        return notice;
    }

    public void setNotice(List<JSONObject> notice) {
        this.notice = notice;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAvatarFileName() {
        return avatarFileName;
    }

    public void setAvatarFileName(String avatarFileName) {
        this.avatarFileName = avatarFileName;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
