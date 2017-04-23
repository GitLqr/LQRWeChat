package com.lqr.wechat.model.request;

/**
 * Created by AMing on 16/1/28.
 * Company RongCloud
 */
public class SetGroupNameRequest {

    private String groupId;

    private String name;

    public SetGroupNameRequest(String groupId, String name) {
        this.groupId = groupId;
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
