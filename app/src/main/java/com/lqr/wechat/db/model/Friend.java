package com.lqr.wechat.db.model;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.litepal.crud.DataSupport;

/**
 * @创建者 CSDN_LQR
 * @描述 朋友表(用户信息表)
 */
public class Friend extends DataSupport implements Comparable<Friend> {

    private String userId;
    private String name;
    private String portraitUri;
    private String displayName;
    private String region;
    private String phoneNumber;
    private String status;
    private Long timestamp;
    private String letters;
    private String nameSpelling;
    private String displayNameSpelling;

    public Friend(String userId, String name, String portraitUri) {
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
        this.displayName = name;
    }

    public Friend(String userId, String name, String portraitUri, String displayName, String region, String phoneNumber, String status, Long timestamp, String nameSpelling, String displayNameSpelling, String letters) {
        this(userId, name, portraitUri);
        this.displayName = displayName;
        this.region = region;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.timestamp = timestamp;
        this.nameSpelling = nameSpelling;
        this.displayNameSpelling = displayNameSpelling;
        this.letters = letters;
    }

    public Friend(String userId, String name, String portraitUri, String displayName, String region, String phoneNumber, String status, Long timestamp, String nameSpelling, String displayNameSpelling) {
        this(userId, name, portraitUri);
        this.displayName = displayName;
        this.region = region;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.timestamp = timestamp;
        this.nameSpelling = nameSpelling;
        this.displayNameSpelling = displayNameSpelling;
    }

    public Friend(String userId, String name, String portraitUri, String displayName, String region, String phoneNumber, String status, Long timestamp) {
        this(userId, name, portraitUri);
        this.displayName = displayName;
        this.region = region;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Friend(String userId, String name, String portraitUri, String displayName, String status, Long timestamp) {
        this(userId, name, portraitUri);
        this.displayName = displayName;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Friend(String userId, String name, String portraitUri, String phoneNumber, String displayName) {
        this(userId, name, portraitUri);
        this.phoneNumber = phoneNumber;
        this.displayName = displayName;
    }

    public Friend(String userId, String name, String portraitUri, String displayName) {
        this(userId, name, portraitUri);
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public String getNameSpelling() {
        return nameSpelling;
    }

    public void setNameSpelling(String nameSpelling) {
        this.nameSpelling = nameSpelling;
    }

    public String getDisplayNameSpelling() {
        return displayNameSpelling;
    }

    public void setDisplayNameSpelling(String displayNameSpelling) {
        this.displayNameSpelling = displayNameSpelling;
    }

    public boolean isExitsDisplayName() {
        return !TextUtils.isEmpty(displayName);
    }

    @Override
    public boolean equals(Object o) {
        if (o != null) {
            Friend friendInfo = (Friend) o;
            return (getUserId() != null && getUserId().equals(friendInfo.getUserId()));
//            return (getUserId() != null && getUserId().equals(friendInfo.getUserId()))
//                    && (getName() != null && getName().equals(friendInfo.getName()))
//                    && (getPortraitUri() != null && getPortraitUri().equals(friendInfo.getPortraitUri()))
//                    && (phoneNumber != null && phoneNumber.equals(friendInfo.getPhoneNumber()))
//                    && (displayName != null && displayName.equals(friendInfo.getDisplayName()));
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(@NonNull Friend friend) {
        return this.getDisplayName().compareTo(friend.getDisplayName());
    }
}