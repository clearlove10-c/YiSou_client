package com.smlz.yisounews.entity;

import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDate;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer userId;

    private String userName;

    private String userPwd;

    private String userSex;

    private LocalDate userBirthday;

    private String userSignature;

    private byte[] userPic;

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public String getUserSex() {
        return userSex;
    }

    public LocalDate getUserBirthday() {
        return userBirthday;
    }

    public String getUserSignature() {
        return userSignature;
    }

    public byte[] getUserPic() {
        return userPic;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public void setUserBirthday(LocalDate userBirthday) {
        this.userBirthday = userBirthday;
    }

    public void setUserSignature(String userSignature) {
        this.userSignature = userSignature;
    }

    public void setUserPic(byte[] userPic) {
        this.userPic = userPic;
    }

    public UserInfo(Integer userId, String userName, String userPwd, String userSex, LocalDate userBirthday, String userSignature, byte[] userPic) {
        this.userId = userId;
        this.userName = userName;
        this.userPwd = userPwd;
        this.userSex = userSex;
        this.userBirthday = userBirthday;
        this.userSignature = userSignature;
        this.userPic = userPic;
    }

    public UserInfo() {
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", userSex='" + userSex + '\'' +
                ", userBirthday=" + userBirthday +
                ", userSignature='" + userSignature + '\'' +
                ", userPic=" + userPic +
                '}';
    }
}
