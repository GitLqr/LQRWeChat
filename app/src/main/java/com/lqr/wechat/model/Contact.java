package com.lqr.wechat.model;

import android.text.TextUtils;

import com.lqr.wechat.nimsdk.NimFriendSDK;
import com.lqr.wechat.nimsdk.NimUserInfoSDK;
import com.lqr.wechat.utils.PinyinUtils;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.Serializable;

/**
 * @创建者 CSDN_LQR
 * @描述 联系人（好友）信息
 */
public class Contact implements Comparable<Contact>, Serializable {

    private String mAccount;//账号
    private String mDisplayName;//要显示的名字（没有备注的话就显示昵称）
    private String mName;//昵称
    private String mAlias;//备注
    private String mPinyin;//昵称/备注的全拼
    private Friend mFriend;//你的好友信息
    private NimUserInfo mUserInfo;//好友自己的信息
    private String mAvatar;//头像地址
//    private List<String> mAccounts;//要查询用户信息的用户账号

    public Contact(Friend friend, NimUserInfo userInfo) {
        mFriend = friend;
        mUserInfo = userInfo;
        fit();
    }

    public Contact(String account) {
        super();
        mFriend = NimFriendSDK.getFriendByAccount(account);
        mUserInfo = NimUserInfoSDK.getUser(account);
        fit();
    }

    public Contact() {
        super();
    }

    private void fit() {
        this.mAccount = mUserInfo.getAccount();
        this.mName = mUserInfo.getName();
        if (mFriend != null)
            this.mAlias = mFriend.getAlias();
        this.mAvatar = mUserInfo.getAvatar();
        this.mDisplayName = TextUtils.isEmpty(mAlias) ? mName : mAlias;
        this.mPinyin = PinyinUtils.getPinyin(mDisplayName);
    }

    public String getAccount() {
        return mAccount;
    }

    public void setAccount(String account) {
        mAccount = account;
    }

    public String getAlias() {
        return mAlias;
    }

    public void setAlias(String alias) {
        this.mAlias = alias;
    }

    public String getPinyin() {
        return mPinyin;
    }

    public void setPinyin(String pinyin) {
        this.mPinyin = pinyin;
    }

    public Friend getFriend() {
        return mFriend;
    }

    public void setFriend(Friend friend) {
        mFriend = friend;
    }

    public NimUserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(NimUserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    @Override
    public int compareTo(Contact o) {
        return this.mPinyin.compareTo(o.getPinyin());
    }
}
