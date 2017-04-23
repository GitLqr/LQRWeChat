package com.lqr.wechat.model.request;

/**
 * Created by AMing on 16/4/1.
 * Company RongCloud
 */
public class JoinGroupRequest {
    private String groupId;

    public JoinGroupRequest(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
