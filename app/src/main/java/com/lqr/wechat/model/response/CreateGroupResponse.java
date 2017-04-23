package com.lqr.wechat.model.response;

/**
 * Created by AMing on 16/1/25.
 * Company RongCloud
 */
public class CreateGroupResponse {


    /**
     * code : 200
     * result : {"id":"ArVtlWJSv"}
     */

    private int code;
    /**
     * id : ArVtlWJSv
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
        private String id;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
