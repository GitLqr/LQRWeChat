package com.lqr.wechat.nimsdk;

import com.lqr.wechat.AppConst;
import com.lqr.wechat.utils.SPUtils;
import com.lqr.wechat.utils.UIUtils;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;

/**
 * @创建者 CSDN_LQR
 * @描述 账号相关SDK
 */
public class NimAccountSDK {

    private static String account;
    private static String token;

    /**
     * 登录，并返回AbortableFuture
     */
    public static AbortableFuture<LoginInfo> login(String username, String token, RequestCallback<LoginInfo> callback) {
        //配置登录信息，并开始登录
        LoginInfo info = new LoginInfo(username, token);
        AbortableFuture<LoginInfo> loginRequest = NIMClient.getService(AuthService.class).login(info);
        loginRequest.setCallback(callback);
        return loginRequest;
    }

    /**
     * 登出
     */
    public static void logout() {
        NIMClient.getService(AuthService.class).logout();
    }

    /**
     * 在线状态监听
     */
    public static void onlineStatusListen(Observer<StatusCode> observer, boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(
                observer, register);
    }

    /**
     * 同步数据监听
     * <p>
     * 登录成功后，SDK 会立即同步数据（用户资料、用户关系、群资料、离线消息、漫游消息等），同步开始和同步完成都会发出通知
     *
     * @param register
     */
    public static void syncDataListen(Observer<LoginSyncStatus> observer, boolean register) {
        NIMClient.getService(AuthServiceObserver.class).observeLoginSyncDataStatus(observer, register);
    }

    public static String getUserAccount() {
        account = SPUtils.getInstance(UIUtils.getContext()).getString(AppConst.Account.KEY_USER_ACCOUNT, "");
        return account;
    }

    public static String getUserToken() {
        token = SPUtils.getInstance(UIUtils.getContext()).getString(AppConst.Account.KEY_USER_TOKEN, "");
        return token;
    }

    public static void saveUserAccount(String account) {
        NimAccountSDK.account = account;
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConst.Account.KEY_USER_ACCOUNT, account);
    }

    public static void saveUserToken(String token) {
        NimAccountSDK.token = token;
        SPUtils.getInstance(UIUtils.getContext()).putString(AppConst.Account.KEY_USER_TOKEN, token);
    }

    public static void removeUserInfo() {
        SPUtils.getInstance(UIUtils.getContext()).remove(AppConst.Account.KEY_USER_ACCOUNT);
        SPUtils.getInstance(UIUtils.getContext()).remove(AppConst.Account.KEY_USER_TOKEN);
    }
}
