package com.lqr.wechat.nimsdk;

import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.List;

/**
 * @创建者 CSDN_LQR
 * @描述 最近联系人相关SDK
 * <p>
 * 最近会话 RecentContact ，也可称作会话列表或者最近联系人列表，
 * 它记录了与用户最近有过会话的联系人信息，包括联系人帐号、联系人类型、最近一条消息的时间、消息状态、消息内容、未读条数等信息。
 * RecentContact 中还提供了一个扩展标签 tag（用于做联系人置顶、最近会话列表排序等扩展用途）和一个扩展字段 extension （是一个Map，可用于做群@等扩展用途），并支持动态的更新这两个字段。
 * 最近会话列表由 SDK 维护并提供查询、监听变化的接口，只要与某个用户或者群组有产生聊天（自己发送消息或者收到消息）， SDK 会自动更新最近会话列表并通知上层，开发者无需手动更新。
 * 某些场景下，开发者可能需要手动向最近会话列表中插入一条会话项（即插入一个最近联系人），
 * 例如：在创建完高级群时，需要在最近会话列表中显示该群的会话项。由创建高级群完成时并不会收到任何消息， SDK 并不会立即更新最近会话，此时要满足需求，可以在创建群成功的回调中，插入一条本地消息， 即调用 MsgService#saveMessageToLocal。
 */
public class NimRecentContactSDK {

    /**
     * 获取最近会话列表
     *
     * @param callback 回调监听（回调中的参数即为最近联系人列表）
     */
    public static void queryRecentContacts(RequestCallbackWrapper<List<RecentContact>> callback) {
        NIMClient.getService(MsgService.class).queryRecentContacts()
                .setCallback(callback);
    }

    /**
     * 注册/注销观察者
     *
     * @param messageObserver 观察者对象
     * @param register        注册/注销
     */
    public static void observeRecentContact(Observer<List<RecentContact>> messageObserver, boolean register) {
        //  注册/注销观察者
        NIMClient.getService(MsgServiceObserve.class)
                .observeRecentContact(messageObserver, register);
    }

    /**
     * 获取会话未读数总数
     *
     * @return
     */
    public static int getTotalUnreadCount() {
        int unreadNum = NIMClient.getService(MsgService.class).getTotalUnreadCount();
        return unreadNum;
    }

    /**
     * 将指定最近联系人的未读数清零(标记已读)。<br>
     * 调用该接口后，会触发{@link MsgServiceObserve#observeRecentContact(Observer, boolean)} 通知
     *
     * @param account     聊天对象帐号
     * @param sessionType 会话类型
     */
    public static void clearUnreadCount(String account, SessionTypeEnum sessionType) {
        NIMClient.getService(MsgService.class).clearUnreadCount(account, sessionType);
    }

    /**
     * 设置当前会话
     * <p>
     * 如果用户在开始聊天时，开发者调用了 setChattingAccount 接口，SDK会自动管理消息的未读数。当收到新消息时，自动将未读数清零
     *
     * @param sessionId
     * @param sessionType
     */
    public static void setChattingAccount(String sessionId, SessionTypeEnum sessionType) {
        NIMClient.getService(MsgService.class).setChattingAccount(sessionId, sessionType);
    }

    /**
     * 移除最近会话列表中的项
     *
     * @param recent
     */
    public static void deleteRecentContact(RecentContact recent) {
        NIMClient.getService(MsgService.class).deleteRecentContact(recent);
    }

    /**
     * 移除最近会话列表中的项，并会触发 MsgServiceObserve#observeRecentContactDeleted 通知
     *
     * @param account
     * @param sessionType
     */
    public static void deleteRecentContactAndNotify(String account, SessionTypeEnum sessionType) {
        NIMClient.getService(MsgService.class).deleteRecentContact2(account, sessionType);
    }

    /**
     * 删除指定最近联系人的漫游消息。
     * 不删除本地消息，但如果在其他端登录，当前时间点该会话已经产生的消息不漫游。
     *
     * @param contactId       最近联系人的ID（好友帐号，群ID等）
     * @param sessionTypeEnum 会话类型
     * @return InvocationFuture 可设置回调函数，监听删除结果。
     */
    public static InvocationFuture<Void> deleteRoamingRecentContact(String contactId, SessionTypeEnum sessionTypeEnum, RequestCallback<Void> callback) {
        InvocationFuture<Void> voidInvocationFuture = NIMClient.getService(MsgService.class)
                .deleteRoamingRecentContact(contactId, sessionTypeEnum);
        voidInvocationFuture.setCallback(callback);
        return voidInvocationFuture;
    }
}
