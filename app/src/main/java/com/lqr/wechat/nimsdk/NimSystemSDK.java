package com.lqr.wechat.nimsdk;

import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;

import java.util.List;

/**
 * @创建者 CSDN_LQR
 * @描述 系统相关SDK
 */
public class NimSystemSDK {


    /**
     * 监听系统通知
     */
    public static void observeReceiveSystemMsg(Observer<SystemMessage> systemMessageObserver, boolean register) {
        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(systemMessageObserver, register);
    }

    /**
     * 查询系统通知列表
     */
    public static List<SystemMessage> querySystemMessagesBlock(int offset, int limit) {
        return NIMClient.getService(SystemMessageService.class)
                .querySystemMessagesBlock(offset, limit);// 参数offset为当前已经查了offset条，limit为要继续查询limit条。
    }

    /**
     * 根据类型查询系统通知列表
     */
    public static InvocationFuture<List<SystemMessage>> querySystemMessageByType(List<SystemMessageType> types, int offset, int limit) {
        return NIMClient.getService(SystemMessageService.class).querySystemMessageByType(types, offset, limit);
    }

    /**
     * 删除一条系统通知
     */
    public static void deleteSystemMessage(SystemMessage message) {
        NIMClient.getService(SystemMessageService.class)
                .deleteSystemMessage(message.getMessageId());
    }

    /**
     * 删除所有系统通知
     */
    public static void clearSystemMessages() {
        NIMClient.getService(SystemMessageService.class).clearSystemMessages();
    }

    /**
     * 删除指定类型的系统通知
     * <p>
     * 如：只删除“添加好友”类型的系统通知
     */
    public static void clearSystemMessagesByType(List<SystemMessageType> types) {
        NIMClient.getService(SystemMessageService.class).clearSystemMessagesByType(types);
    }

    /**
     * 查询系统通知未读数总和
     */
    public int querySystemMessageUnreadCountBlock() {
        int unread = NIMClient.getService(SystemMessageService.class)
                .querySystemMessageUnreadCountBlock();
        return unread;
    }

    /**
     * 查询指定类型的系统通知未读数总和
     * <p>
     * 如：查询“添加好友”类型的系统通知未读数总和
     */
    public static int querySystemMessageUnreadCountByType(List<SystemMessageType> types) {
        int unread = NIMClient.getService(SystemMessageService.class)
                .querySystemMessageUnreadCountByType(types);
        return unread;
    }

    /**
     * 设置单条系统通知为已读
     */
    public static void setSystemMessageRead(long messageId) {
        NIMClient.getService(SystemMessageService.class).setSystemMessageRead(messageId);
    }

    /**
     * 将所有系统通知设为已读
     * <p>
     * 如：进入过系统通知列表后，可调用此函数将未读数值为0
     */
    public static void resetSystemMessageUnreadCount() {
        NIMClient.getService(SystemMessageService.class).resetSystemMessageUnreadCount();
    }

    /**
     * 将指定类型的系统通知设为已读接口
     * <p>
     * 如：将“添加好友”类型的系统通知设为已读
     */
    public static void resetSystemMessageUnreadCount(List<SystemMessageType> types) {
        NIMClient.getService(SystemMessageService.class).resetSystemMessageUnreadCountByType(types);
    }
}
