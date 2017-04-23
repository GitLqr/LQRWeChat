package com.lqr.wechat.model.response;

import java.util.List;

/**
 * Created by AMing on 16/5/23.
 * Company RongCloud
 */
public class GetUserInfosResponse {

    /**
     * code : 200
     * result : [{"id":"t1hWCOGvX","nickname":"阿明","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/Fq_AkfurWIXCDnclIEZBwEoc988R"},{"id":"LEU82p5Zk","nickname":"李涛","portraitUri":""},{"id":"jkirN8Yfq","nickname":"李小黎","portraitUri":""}]
     */

    private int code;
    /**
     * id : t1hWCOGvX
     * nickname : 阿明
     * portraitUri : http://7xogjk.com1.z0.glb.clouddn.com/Fq_AkfurWIXCDnclIEZBwEoc988R
     */

    private List<ResultEntity> result;

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(List<ResultEntity> result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public List<ResultEntity> getResult() {
        return result;
    }

    public static class ResultEntity {
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
