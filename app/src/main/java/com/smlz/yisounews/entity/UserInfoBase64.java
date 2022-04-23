package com.smlz.yisounews.entity;

import com.google.gson.annotations.SerializedName;
import com.smlz.yisounews.util.ImageUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserInfoBase64  {

    private Integer userId;

    private String userName;

    private String userPwd;

    private String userSex;

    private String userBirthday;

    private String userSignature;
    @SerializedName("userPic")
    private String userPicBase64;

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

    public String getUserBirthday() {
        return userBirthday;
    }

    public String getUserSignature() {
        return userSignature;
    }

    public String getUserPic() {
        return userPicBase64;
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

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public void setUserSignature(String userSignature) {
        this.userSignature = userSignature;
    }

    public void setUserPic(String userPic) {
        this.userPicBase64 = userPic;
    }

    public UserInfoBase64(Integer userId, String userName, String userPwd, String userSex, String userBirthday, String userSignature, String userPic) {
        this.userId = userId;
        this.userName = userName;
        this.userPwd = userPwd;
        this.userSex = userSex;
        this.userBirthday = userBirthday;
        this.userSignature = userSignature;
        this.userPicBase64 = userPic;
    }

    public UserInfoBase64() {
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
                ", userPic=" + userPicBase64 +
                '}';
    }

    public static UserInfoBase64 toUserInfoBase64(UserInfo userInfo) {
        return new UserInfoBase64(userInfo.getUserId(), userInfo.getUserName(), userInfo.getUserPwd(), userInfo.getUserSex(),
                userInfo.getUserBirthday().toString(), userInfo.getUserSignature(), userInfo.getUserPic() == null ? null : ImageUtil.encode(userInfo.getUserPic()));
    }
    public static UserInfo toUserInfo(UserInfoBase64 userInfo) {
        try {
            return new UserInfo(userInfo.getUserId(),
                    userInfo.getUserName(),
                    userInfo.getUserPwd(),
                    userInfo.getUserSex(),
                    userInfo.getUserBirthday()==null?null: LocalDate.parse(userInfo.getUserBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    userInfo.getUserSignature(),
                    userInfo.getUserPic() == null ? null : ImageUtil.decode(userInfo.getUserPic()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
