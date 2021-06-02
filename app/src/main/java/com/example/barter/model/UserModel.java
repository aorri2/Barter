package com.example.barter.model;


public class UserModel {
    public String userName;
    public String userPhone;
    public static String profileImageUrl;
    public String uid;
    public String pushToken;
    public String comment;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public static String getProfileImageUrl() {
        return profileImageUrl;
    }

    public static void setProfileImageUrl(String userImages) {
        UserModel.profileImageUrl = userImages;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}