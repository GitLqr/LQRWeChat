package com.lqr.wechat.model.request;


/**
 * Created by AMing on 16/1/8.
 * Company RongCloud
 */
public class AgreeFriendsRequest {

    private String friendId;

    public AgreeFriendsRequest(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
}
