package com.lqr.wechat.model.response;


/**
 * Created by AMing on 15/12/22.
 * Company RongCloud
 */
public class VerifyCodeResponse {


    /**
     * code : 200
     * result : {"verification_token":"86bd3a00-b80a-11e5-b5ab-433619959d67"}
     */

    private int code;
    /**
     * verification_token : 86bd3a00-b80a-11e5-b5ab-433619959d67
     */

    private ResultEntity result;

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public ResultEntity getResult() {
        return result;
    }

    public static class ResultEntity {
        private String verification_token;

        public void setVerification_token(String verification_token) {
            this.verification_token = verification_token;
        }

        public String getVerification_token() {
            return verification_token;
        }
    }
}
