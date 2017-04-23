package com.lqr.wechat.db.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * @创建者 CSDN_LQR
 * @描述 群组
 */

public class Groups extends DataSupport implements Serializable {

    private String groupId;
    private String name;
    private String portraitUri;
    private String displayName;
    private String role;//0是管理员，1是群成员
    private String bulletin;
    private String timestamp;
    private String nameSpelling;

    public Groups(String groupsId) {
        this.groupId = groupsId;
    }

    public Groups(String groupsId, String name, String portraitUri) {
        this.groupId = groupsId;
        this.name = name;
        this.portraitUri = portraitUri;
    }

    public Groups(String groupsId, String name, String portraitUri, String displayName, String role, String bulletin, String timestamp) {
        this(groupsId, name, portraitUri);
        this.displayName = displayName;
        this.role = role;
        this.bulletin = bulletin;
        this.timestamp = timestamp;
    }

    public Groups(String timestamp, String role, String displayName, String portraitUri, String name, String groupsId) {
        this(groupsId, name, portraitUri);
        this.timestamp = timestamp;
        this.role = role;
        this.displayName = displayName;
    }

    public Groups(String groupsId, String name, String portraitUri, String role) {
        this(groupsId, name, portraitUri);
        this.role = role;
    }

    public Groups(String groupsId, String name, String portraitUri, String displayName, String role, String bulletin, String timestamp, String nameSpelling) {
        this(groupsId, name, portraitUri);
        this.displayName = displayName;
        this.role = role;
        this.bulletin = bulletin;
        this.timestamp = timestamp;
        this.nameSpelling = nameSpelling;
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

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBulletin() {
        return bulletin;
    }

    public void setBulletin(String bulletin) {
        this.bulletin = bulletin;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNameSpelling() {
        return nameSpelling;
    }

    public void setNameSpelling(String nameSpelling) {
        this.nameSpelling = nameSpelling;
    }
}
