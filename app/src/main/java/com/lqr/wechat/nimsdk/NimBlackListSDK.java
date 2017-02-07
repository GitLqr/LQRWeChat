package com.lqr.wechat.nimsdk;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.FriendServiceObserve;
import com.netease.nimlib.sdk.friend.model.BlackListChangedNotify;

import java.util.List;

/**
 * @创建者 CSDN_LQR
 * @描述 黑名单相关SDK
 */
public class NimBlackListSDK {

    /**
     * 加入黑名单
     */
    public static void addToBlackList(String account, RequestCallback<Void> callback) {
        NIMClient.getService(FriendService.class).addToBlackList(account)
                .setCallback(callback);
    }

    /**
     * 移出黑名单
     */
    public static void removeFromBlackList(String account, RequestCallback<Void> callback) {
        NIMClient.getService(FriendService.class).removeFromBlackList(account)
                .setCallback(callback);
    }

    /**
     * 获取黑名单列表
     */
    public static List<String> getBlackList() {
        return NIMClient.getService(FriendService.class).getBlackList();
    }

    /**
     * 判断用户是否被拉进黑名单
     */
    public static boolean isInBlackList(String account) {
        boolean black = NIMClient.getService(FriendService.class).isInBlackList(account);
        return black;
    }

    /**
     * 注册/注销黑名单变化监听
     */
    public static void observeBlackListChangedNotify(Observer<BlackListChangedNotify> blackListChangedNotifyObserver, boolean register) {
        NIMClient.getService(FriendServiceObserve.class)
                .observeBlackListChangedNotify(blackListChangedNotifyObserver, register);
    }

}
