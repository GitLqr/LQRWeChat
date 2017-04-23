package com.lqr.wechat.model.request;


/**
 * Created by AMing on 15/12/23.
 * Company RongCloud
 */
public class SendCodeRequest {

    private String region;

    private String phone;

    public SendCodeRequest(String region, String phone) {
        this.region = region;
        this.phone = phone;
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
}
