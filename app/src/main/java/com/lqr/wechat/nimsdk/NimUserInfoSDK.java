package com.lqr.wechat.nimsdk;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.UserServiceObserve;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @创建者 CSDN_LQR
 * @描述 网易云信用户信息相关sdk
 */
public class NimUserInfoSDK {

    /*================== 获取用户信息 ==================*/

    /**
     * 通过用户帐号集合，从本地数据库批量获取用户资料列表
     */
    public static List<NimUserInfo> getUsers(List<String> accounts) {
        List<NimUserInfo> users = NIMClient.getService(UserService.class).getUserInfoList(accounts);
        return users;
    }

    /**
     * 通过用户账号，从本地数据库获取用户资料
     */
    public static NimUserInfo getUser(String account) {
        NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(account);
        return user;
    }

    /**
     * 获取本地数据库中所有的用户资料，一般适合在登录后构建用户资料缓存时使用
     *
     * @param account
     */
    public static List<NimUserInfo> getUsers(String account) {
        List<NimUserInfo> users = NIMClient.getService(UserService.class).getAllUserInfo();
        return users;
    }

    /**
     * 构建通讯录
     * <p>
     * 如果使用网易云信用户关系、用户资料托管，构建通讯录，先获取我所有好友帐号，再根据帐号去获取对应的用户资料
     */
    public static List<NimUserInfo> getContacts() {
        List<String> accounts = NIMClient.getService(FriendService.class).getFriendAccounts(); // 获取所有好友帐号
        List<NimUserInfo> users = NIMClient.getService(UserService.class).getUserInfoList(accounts); // 获取所有好友用户资料
        return users;
    }

    /**
     * 获取服务器用户资料(常用于实时要求高的场景)
     * <p>
     * 从服务器获取用户资料，一般在本地用户资料不存在时调用，获取后 SDK 会负责更新本地数据库
     */
    public static void getUserInfosFormServer(List<String> accounts, RequestCallback<List<NimUserInfo>> callback) {
        NIMClient.getService(UserService.class).fetchUserInfo(accounts)
                .setCallback(callback);
    }

    /**
     * 获取单个用户的信息（可以用来做搜索用户）
     */
    public static void getUserInfoFromServer(String account, RequestCallback<List<NimUserInfo>> callback) {
        List<String> accounts = new ArrayList<>();
        accounts.add(account);
        getUserInfosFormServer(accounts, callback);
    }

    /*================== 更新用户信息 ==================*/

    /**
     * 上传文件到网易云信云存储
     */
    public static void uploadFile(File file, String mimeType, RequestCallbackWrapper<String> callback) {
        NIMClient.getService(NosService.class).upload(file, mimeType)
                .setCallback(callback);
    }

    /**
     * 更新用户本人资料
     * <p>
     * 传入参数 Map<UserInfoFieldEnum, Object> 更新用户本人资料，key 为字段，value 为对应的值。
     * 具体字段见 UserInfoFieldEnum，包括：昵称，性别，头像 URL，签名，手机，邮箱，生日以及扩展字段等。
     */
    public static void updateUserInfo(Map<UserInfoFieldEnum, Object> fields, RequestCallbackWrapper<Void> callback) {
        NIMClient.getService(UserService.class).updateUserInfo(fields)
                .setCallback(callback);
    }

    /**
     * 注册/注销观察者
     */
    public static void observeUserInfoUpdate(Observer<List<NimUserInfo>> userInfoUpdateObserver, boolean register) {
        NIMClient.getService(UserServiceObserve.class).observeUserInfoUpdate(userInfoUpdateObserver, register);
    }

}
