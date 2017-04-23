package com.lqr.wechat.model.response;

import java.io.Serializable;

/**
 * Created by AMing on 16/1/4.
 * Company RongCloud
 */
public class GetUserInfoByPhoneResponse {


    /**
     * code : 200
     * result : {"id":"10YVscJI3","nickname":"阿明","portraitUri":""}
     */

    private int code;
    /**
     * id : 10YVscJI3
     * nickname : 阿明
     * portraitUri :
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

    public static class ResultEntity implements Serializable {
        private String id;
        private String nickname;
        private String portraitUri;

        public void setId(String id) {
            this.id = id;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setPortraitUri(String portraitUri) {
            this.portraitUri = portraitUri;
        }

        public String getId() {
            return id;
        }

        public String getNickname() {
            return nickname;
        }

        public String getPortraitUri() {
            return portraitUri;
        }
    }
}
