package com.lqr.wechat.model.request;

/**
 * Created by AMing on 16/1/13.
 * Company RongCloud
 */
public class SetPortraitRequest {

    private String portraitUri;


    public SetPortraitRequest(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }
}
