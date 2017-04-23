package com.lqr.wechat.model.request;

import java.util.List;

/**
 * Created by AMing on 16/1/25.
 * Company RongCloud
 */
public class CreateGroupRequest {

    private String name;

    private List<String> memberIds;

    public CreateGroupRequest(String name, List<String> memberIds) {
        this.name = name;
        this.memberIds = memberIds;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
