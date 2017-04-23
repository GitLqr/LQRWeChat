package com.lqr.wechat.model.request;

/**
 * Created by AMing on 16/1/25.
 * Company RongCloud
 */
public class SetGroupPortraitRequest {

    private String groupId;
    private String portraitUri;

    public SetGroupPortraitRequest(String groupId, String portraitUri) {
        this.groupId = groupId;
        this.portraitUri = portraitUri;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }
}
