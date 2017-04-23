package com.lqr.wechat.model.request;


/**
 * Created by AMing on 15/12/24.
 * Company RongCloud
 */
public class LoginRequest {


    private String region;
    private String phone;
    private String password;

    public LoginRequest(String region, String phone, String password) {
        this.region = region;
        this.phone = phone;
        this.password = password;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
