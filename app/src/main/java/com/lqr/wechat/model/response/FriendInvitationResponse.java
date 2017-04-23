package com.lqr.wechat.model.response;


/**
 * Created by AMing on 16/1/7.
 * Company RongCloud
 */
public class FriendInvitationResponse {

    /**
     * code : 200
     * result : {"action":"Sent"}
     * message : Request sent.
     */

    private int code;
    /**
     * action : Sent
     */

    private ResultEntity result;
    private String message;

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public ResultEntity getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public static class ResultEntity {
        private String action;

        public void setAction(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }
    }
}
