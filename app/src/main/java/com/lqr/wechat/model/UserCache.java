package com.lqr.wechat.model;

import android.content.Context;

import com.lqr.wechat.nimsdk.NimAccountSDK;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;

/**
 * @创建者 CSDN_LQR
 * @描述 用户信息缓存
 */
public class UserCache {

    private static Context context;
    private static String account;

    private static StatusBarNotificationConfig notificationConfig;

    public static void clear() {
        account = null;
        NimAccountSDK.removeUserInfo();
    }

    public static String getAccount() {
        return account;
    }

    public static void setAccount(String account) {
        UserCache.account = account;
    }

    public static void setNotificationConfig(StatusBarNotificationConfig notificationConfig) {
        UserCache.notificationConfig = notificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        UserCache.context = context.getApplicationContext();
    }
}
