package com.lqr.wechat.model.request;


/**
 * Created by AMing on 15/12/24.
 * Company RongCloud
 */
public class RestPasswordRequest {

    /**
     * password : asdfas
     * verification_token : 548646a0-b5f1-11e5-b5ab-433619959d67
     */

    private String password;
    private String verification_token;

    public void setPassword(String password) {
        this.password = password;
    }

    public RestPasswordRequest(String password, String verification_token) {
        this.password = password;
        this.verification_token = verification_token;
    }

    public void setVerification_token(String verification_token) {
        this.verification_token = verification_token;
    }

    public String getPassword() {
        return password;
    }

    public String getVerification_token() {
        return verification_token;
    }
}
