package com.lqr.wechat.model.request;

/**
 * Created by AMing on 16/1/29.
 * Company RongCloud
 */
public class QuitGroupRequest {

    private String groupId;

    public QuitGroupRequest(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
