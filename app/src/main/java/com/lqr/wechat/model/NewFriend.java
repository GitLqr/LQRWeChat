package com.lqr.wechat.model;

import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

/**
 * @创建者 CSDN_LQR
 * @描述 新好友
 */
public class NewFriend {
    private NimUserInfo mUserInfo;
    private String mMsg;

    public NewFriend(NimUserInfo userInfo, String msg) {
        mUserInfo = userInfo;
        mMsg = msg;
    }

    public NimUserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(NimUserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }
}
