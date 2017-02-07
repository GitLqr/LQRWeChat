package com.lqr.wechat.nimsdk;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.FriendServiceObserve;
import com.netease.nimlib.sdk.friend.constant.FriendFieldEnum;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.friend.model.FriendChangedNotify;

import java.util.List;
import java.util.Map;

/**
 * @创建者 CSDN_LQR
 * @描述 网易云信好友相关SDK
 */
public class NimFriendSDK {


    /**
     * 添加好友
     * <p>
     * 目前添加好友有两种验证类型（见 VerifyType）：直接添加为好友和发起好友验证请求。添加好友时需要构造 AddFriendData，
     * 需要填入包括对方帐号，好友验证类型及附言（可选）
     */
    public static void addFriend(String account, String msg, RequestCallback<Void> callback) {
        final VerifyType verifyType = VerifyType.VERIFY_REQUEST; // 发起好友验证请求
        NIMClient.getService(FriendService.class).addFriend(new AddFriendData(account, verifyType, msg))
                .setCallback(callback);
    }

    /**
     * 通过/拒绝对方好友请求
     * <p>
     * 收到好友的验证请求的系统通知后，可以通过或者拒绝，对方会收到一条系统通知，通知的事件类型为 AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND 或者 AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND
     */
    public static void ackAddFriendRequest(String account, boolean agree) {
        NIMClient.getService(FriendService.class).ackAddFriendRequest(account, true); // 通过对方的好友请求
    }

    /**
     * 获取好友账号列表
     * <p>
     * 该方法是同步方法，返回我的好友帐号集合，可以根据帐号来获取对应的用户资料来构建自己的通讯录,见构建通讯录
     */
    public static List<String> getFriendAccounts() {
        List<String> friendAccounts = NIMClient.getService(FriendService.class).getFriendAccounts();
        return friendAccounts;
    }

    /**
     * 获取好友信息列表
     */
    public static List<Friend> getFriends() {
        List<Friend> friends = NIMClient.getService(FriendService.class).getFriends();
        return friends;
    }

    /**
     * 删除好友
     * <p>
     * 删除好友后，将自动解除双方的好友关系，双方的好友列表中均不存在对方。删除好友后，双方依然可以聊天。
     */
    public static void deleteFriend(String account, RequestCallback<Void> callback) {
        NIMClient.getService(FriendService.class).deleteFriend(account)
                .setCallback(callback);
    }

    /**
     * 根据用户账号获取好友
     */
    public static Friend getFriendByAccount(String account) {
        Friend friend = NIMClient.getService(FriendService.class).getFriendByAccount(account);
        return friend;
    }

    /**
     * 判断用户是否为我的好友
     */
    public static boolean isMyFriend(String account) {
        boolean isMyFriend = NIMClient.getService(FriendService.class).isMyFriend(account);
        return isMyFriend;
    }

    /**
     * 更新好友关系
     * 目前支持更新好友的备注名和好友关系扩展字段，见 FriendFieldEnum。
     */
    public static void updateFriendFields(String account, Map<FriendFieldEnum, Object> map, RequestCallback<Void> callback) {
        NIMClient.getService(FriendService.class).updateFriendFields(account, map)
                .setCallback(callback);
    }

    /**
     * 监听好友关系的变化
     * <p>
     * 第三方 APP 应在 APP 启动后监听好友关系的变化，当主动添加好友成功、被添加为好友、主动删除好友成功、被对方解好友关系、好友关系更新、登录同步好友关系数据时都会收到通知：
     */
    public static void observeFriendChangedNotify(Observer<FriendChangedNotify> friendChangedNotifyObserver, boolean register) {
        NIMClient.getService(FriendServiceObserve.class).observeFriendChangedNotify(friendChangedNotifyObserver, register);
    }


}
