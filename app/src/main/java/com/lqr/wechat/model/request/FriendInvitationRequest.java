package com.lqr.wechat.model.request;


/**
 * Created by AMing on 16/1/7.
 * Company RongCloud
 */
public class FriendInvitationRequest {
    private String friendId;
    private String message;

    public FriendInvitationRequest(String userid, String addFrinedMessage) {
        this.message = addFrinedMessage;
        this.friendId = userid;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
