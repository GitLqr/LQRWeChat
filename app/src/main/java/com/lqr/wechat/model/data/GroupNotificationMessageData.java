package com.lqr.wechat.model.data;

import java.util.ArrayList;
import java.util.List;

public class GroupNotificationMessageData {
    private long timestamp;
    private String operatorNickname;
    private String targetGroupName;
    private List<String> targetUserDisplayNames = new ArrayList();
    private List<String> targetUserIds = new ArrayList();
    private String oldCreatorId;
    private String oldCreatorName;
    private String newCreatorId;
    private String newCreatorName;

    public GroupNotificationMessageData() {
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setOperatorNickname(String operatorNickname) {
        this.operatorNickname = operatorNickname;
    }

    public void setTargetUserDisplayNames(List<String> targetUserDisplayNames) {
        this.targetUserDisplayNames = targetUserDisplayNames;
    }

    public void setTargetUserIds(List<String> targetUserIds) {
        this.targetUserIds = targetUserIds;
    }

    public void setTargetGroupName(String targetGroupName) {
        this.targetGroupName = targetGroupName;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getOperatorNickname() {
        return this.operatorNickname;
    }

    public String getTargetGroupName() {
        return this.targetGroupName;
    }

    public List<String> getTargetUserDisplayNames() {
        return this.targetUserDisplayNames;
    }

    public List<String> getTargetUserIds() {
        return this.targetUserIds;
    }

    public String getOldCreatorId() {
        return this.oldCreatorId;
    }

    public void setOldCreatorId(String oldCreatorId) {
        this.oldCreatorId = oldCreatorId;
    }

    public String getOldCreatorName() {
        return this.oldCreatorName;
    }

    public void setOldCreatorName(String oldCreatorName) {
        this.oldCreatorName = oldCreatorName;
    }

    public String getNewCreatorId() {
        return this.newCreatorId;
    }

    public void setNewCreatorId(String newCreatorId) {
        this.newCreatorId = newCreatorId;
    }

    public String getNewCreatorName() {
        return this.newCreatorName;
    }

    public void setNewCreatorName(String newCreatorName) {
        this.newCreatorName = newCreatorName;
    }
}