package com.lqr.wechat.model.request;

/**
 * Created by AMing on 16/3/4.
 * Company RongCloud
 */
public class RemoveFromBlacklistRequest {

    private String friendId;

    public RemoveFromBlacklistRequest(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
}
