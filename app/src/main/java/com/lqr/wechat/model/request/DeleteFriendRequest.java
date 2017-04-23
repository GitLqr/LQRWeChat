package com.lqr.wechat.model.request;

/**
 * Created by AMing on 16/2/17.
 * Company RongCloud
 */
public class DeleteFriendRequest {
    private String friendId;

    public DeleteFriendRequest(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
}
