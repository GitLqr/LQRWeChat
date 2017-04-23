package com.lqr.wechat.model.response;

/**
 * Created by AMing on 16/3/31.
 * Company RongCloud
 */
public class QiNiuTokenResponse {


    /**
     * code : 200
     * result : {"target":"qiniu","domain":"7xogjk.com1.z0.glb.clouddn.com","token":"livk5rb3__JZjCtEiMxXpQ8QscLxbNLehwhHySnX:eNVE-zcecMiHjRpOvK1txr2-LgY=:eyJzY29wZSI6InNlYWx0YWxrLWltYWdlIiwiZGVhZGxpbmUiOjE0NTk4MjgzMTh9"}
     */

    private int code;
    /**
     * target : qiniu
     * domain : 7xogjk.com1.z0.glb.clouddn.com
     * token : livk5rb3__JZjCtEiMxXpQ8QscLxbNLehwhHySnX:eNVE-zcecMiHjRpOvK1txr2-LgY=:eyJzY29wZSI6InNlYWx0YWxrLWltYWdlIiwiZGVhZGxpbmUiOjE0NTk4MjgzMTh9
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
        private String target;
        private String domain;
        private String token;

        public void setTarget(String target) {
            this.target = target;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getTarget() {
            return target;
        }

        public String getDomain() {
            return domain;
        }

        public String getToken() {
            return token;
        }
    }
}
