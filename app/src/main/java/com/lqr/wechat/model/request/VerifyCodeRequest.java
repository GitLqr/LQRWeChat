package com.lqr.wechat.model.request;


/**
 * Created by AMing on 15/12/23.
 * Company RongCloud
 */
public class VerifyCodeRequest {


    //String region, String phone, String code

    private String region;

    private String phone;

    private String code;

    public VerifyCodeRequest(String region, String phone, String code) {
        this.region = region;
        this.phone = phone;
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
