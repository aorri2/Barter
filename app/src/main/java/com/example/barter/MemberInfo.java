package com.example.barter;

public class MemberInfo {
    private String name;
    private String phoneNum;
    private String date;
    private String address;
    private String photoUrl;

    public MemberInfo(String name, String phoneNum, String date, String address, String photoUrl) {
        this.name = name;
        this.phoneNum = phoneNum;
        this.date = date;
        this.address = address;
        this.photoUrl = photoUrl;
    }

    public MemberInfo(String name, String phoneNum, String date, String address) {
        this.name = name;
        this.phoneNum = phoneNum;
        this.date = date;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
