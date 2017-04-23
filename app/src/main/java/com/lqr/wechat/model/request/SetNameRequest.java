package com.lqr.wechat.model.request;

/**
 * Created by AMing on 16/1/18.
 * Company RongCloud
 */
public class SetNameRequest {

    private String nickname;

    public SetNameRequest(String nickname) {
        this.nickname = nickname;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
