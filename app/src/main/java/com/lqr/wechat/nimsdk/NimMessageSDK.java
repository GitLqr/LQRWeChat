package com.lqr.wechat.nimsdk;

import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.lqr.wechat.utils.UIUtils;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @创建者 CSDN_LQR
 * @描述 网易云信消息相关SDK
 */
public class NimMessageSDK {

    /**
     * 创建文本消息
     *
     * @param sessionId   聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
     * @param sessionType 聊天类型，单聊或群组
     * @param content     文本内容
     * @return
     */
    public static IMMessage createTextMessage(String sessionId, SessionTypeEnum sessionType, String content) {
        return MessageBuilder.createTextMessage(
                sessionId,
                sessionType,
                content
        );
    }

    /**
     * 创建地理位置消息
     *
     * @param sessionId   聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
     * @param sessionType 聊天类型，单聊或群组
     * @param latitude    纬度
     * @param longitude   经度
     * @param address     地址信息描述
     * @return
     */
    public static IMMessage createLocationMessage(String sessionId, SessionTypeEnum sessionType, double latitude, double longitude, String address) {
        return MessageBuilder.createLocationMessage(
                sessionId,
                sessionType,
                latitude,
                longitude,
                address
        );
    }

    /**
     * 创建图片消息
     *
     * @param sessionId   聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
     * @param sessionType 聊天类型，单聊或群组
     * @param file        图片文件对象
     * @param displayName 文件显示名字，如果第三方 APP 不关注，可以为 null
     * @return
     */
    public static IMMessage createImageMessage(String sessionId, SessionTypeEnum sessionType, File file, String displayName) {
        return MessageBuilder.createImageMessage(
                sessionId,
                sessionType,
                file,
                displayName
        );
    }

    /**
     * 创建音频消息
     *
     * @param sessionId   聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
     * @param sessionType 聊天类型，单聊或群组
     * @param file        音频文件
     * @param duration    音频持续时间，单位是ms
     * @return
     */
    public static IMMessage createAudioMessage(String sessionId, SessionTypeEnum sessionType, File file, long duration) {
        return MessageBuilder.createAudioMessage(
                sessionId,
                sessionType,
                file,
                duration
        );
    }

    /**
     * 创建音频消息(不知道行不行)
     *
     * @param sessionId   聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
     * @param sessionType 聊天类型，单聊或群组
     * @param file        音频文件
     * @return
     */
    public static IMMessage createAudioMessage(String sessionId, SessionTypeEnum sessionType, File file) {
        MediaPlayer mediaPlayer = getVideoMediaPlayer(file);
        long duration = mediaPlayer == null ? 0 : mediaPlayer.getDuration();
        return createAudioMessage(sessionId, sessionType, file, duration);
    }

    /**
     * 创建视频消息
     *
     * @param sessionId   聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
     * @param sessionType 聊天类型，单聊或群组
     * @param file        视频文件
     * @param displayName 视频显示名，可为空
     * @return
     */
    public static IMMessage createVideoMessage(String sessionId, SessionTypeEnum sessionType, File file, String displayName) {
        MediaPlayer mediaPlayer = getVideoMediaPlayer(file);
        long duration = mediaPlayer == null ? 0 : mediaPlayer.getDuration();
        int height = mediaPlayer == null ? 0 : mediaPlayer.getVideoHeight();
        int width = mediaPlayer == null ? 0 : mediaPlayer.getVideoWidth();
        return MessageBuilder.createVideoMessage(
                sessionId,
                sessionType,
                file,
                duration, // 视频持续时间
                width, // 视频宽度
                height, // 视频高度
                displayName
        );
    }

    /**
     * // 创建提醒消息（主要用于会话内的通知提醒，例如进入会话时出现的欢迎消息，
     * // 或是会话过程中命中敏感词后的提示消息等场景，也可以用自定义消息实现，但相对于Tip消息实现比较复杂）
     * // 注意：提醒消息不支持setAttachment（如果要使用Attachment请使用自定义消息）。
     *
     * @param sessionId   聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
     * @param sessionType 聊天类型，单聊或群组
     * @param content     提示内容，可以为空
     * @return
     */
    public static IMMessage createTipMessage(String sessionId, SessionTypeEnum sessionType, String content) {
        IMMessage message = MessageBuilder.createTipMessage(
                sessionId,
                sessionType
        );
        if (!TextUtils.isEmpty(content))
            message.setContent(content);
        return message;
    }

    /**
     * 创建自定义消息
     *
     * @param sessionId   聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
     * @param sessionType 聊天类型，单聊或群组
     * @param content     文本内容
     * @param attachment  附件
     * @return
     */
    public static IMMessage createCustomMessage(String sessionId, SessionTypeEnum sessionType, String content, MsgAttachment attachment) {
        return MessageBuilder.createCustomMessage(sessionId, sessionType, content, attachment);
    }

    /**
     * 设置服务器扩展字段
     *
     * @param message 消息对象
     * @param data    扩展字段
     * @return
     */
    public static IMMessage setRemoteExtension(IMMessage message, Map data) {
        message.setRemoteExtension(data);
        return message;
    }

    /**
     * 设置本地扩展字段
     *
     * @param message 消息对象
     * @param data    扩展字段
     * @return
     */
    public static IMMessage setLocalExtension(IMMessage message, Map data) {
        message.setLocalExtension(data);
        return message;
    }

    /**
     * 自定义推送属性
     *
     * @param message 消息对象
     * @param data    推送属性
     * @return
     */
    public static IMMessage setPushPayload(IMMessage message, Map data) {
        message.setPushPayload(data);
        return message;
    }

    /**
     * 设置推送内容
     *
     * @param message     消息对象
     * @param pushContent 推送内容
     * @return
     */
    public static IMMessage setPushContent(IMMessage message, String pushContent) {
        message.setPushContent(pushContent);
        return message;
    }

    /**
     * 发送消息
     *
     * @param message 消息对象
     * @param resend  如果是发送失败后重发，标记为true，否则填false
     */
    private static void sendMessage(IMMessage message, boolean resend) {
        NIMClient.getService(MsgService.class).sendMessage(message, resend);
    }

    /**
     * 发送消息
     *
     * @param message
     */
    public static void sendMessage(IMMessage message) {
        sendMessage(message, false);
    }

    /**
     * 重发消息
     *
     * @param message
     */
    public static void reSendMessage(IMMessage message) {
        sendMessage(message, true);
    }

    /**
     * 转发消息，网易云信支持消息转发功能，不支持通知消息和音视频消息的转发，其他消息类型均支持。
     *
     * @param forwardMessage  想转发的消息
     * @param sessionId       转发目标的聊天对象id
     * @param sessionTypeEnum 转发目标的会话类型
     */
    public static void forwardMessage(IMMessage forwardMessage, String sessionId, SessionTypeEnum sessionTypeEnum) {
        // 创建待转发消息
        IMMessage message = MessageBuilder.createForwardMessage(forwardMessage, sessionId, sessionTypeEnum);
        if (message == null) {
            Toast.makeText(UIUtils.getContext(), "该类型不支持转发", Toast.LENGTH_SHORT).show();
            return;
        }
        sendMessage(message, false);
    }

    /**
     * 保存消息到本地
     * 1. 如果第三方APP想保存消息到本地，可以调用 MsgService#saveMessageToLocal ，该接口保存消息到本地数据库，但不发送到服务器端。该接口将消息保存到数据库后，如果需要通知到UI，可将参数 notify 设置为 true ，此时会触发 #observeReceiveMessage 通知。
     * 2. 此接口在 1.8.0 版本及以上支持设置是否计入未读数（默认计入未读数），若需要不计入未读数，传入的 IMMessage 中的 CustomMessageConfig 的 enableUnreadCount 需要设置为 false 。
     *
     * @param message
     * @param nofity  是否要提醒
     */
    public static void saveMessageToLocal(IMMessage message, boolean nofity) {
        NIMClient.getService(MsgService.class).saveMessageToLocal(message, nofity);
    }

    /**
     * 注册/注销消息状态变化观察者
     * <p>
     * 如果是观察接收状态，一般如下步骤：
     * // 1、根据sessionId判断是否是自己的消息
     * // 2、更改内存中消息的状态
     * // 3、刷新界面
     *
     * @param observer 监听回调（回调中的参数为有状态发生改变的消息对象，其 msgStatus 和 attachStatus 均为最新状态。发送消息和接收消息的状态监听均可以通过此接口完成。）
     * @param register true为注册，false为注销
     */
    public static void observeMsgStatus(Observer<IMMessage> observer, boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(observer, register);
    }

    /**
     * 如果发送的多媒体文件消息，还需要监听文件的上传进度。
     *
     * @param observer 监听回调（回调中的参数为附件的传输进度，可根据 progress 中的 uuid 查找具体的消息对象，更新 UI。上传附件和下载附件的进度监听均可以通过此接口完成。)
     */
    public static void observeAttachProgress(Observer observer, boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeAttachmentProgress(observer, register);
    }

    /**
     * 接收消息
     * 通过添加消息接收观察者，在有新消息到达时，第三方 APP 就可以接收到通知
     * <p>
     * 该代码的典型场景为消息对话界面，在界面 onCreate 里注册消息接收观察者，在 onDestroy 中注销观察者。在收到消息时，判断是否是当前聊天对象的消息，如果是，加入到列表中显示
     *
     * @param incomingMessageObserver 消息接收观察者
     */
    public static void observeReceiveMessage(Observer<List<IMMessage>> incomingMessageObserver, boolean register) {
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, register);
    }

    /**
     * 判断附件是否已经下载过
     * <p>
     * 下载之前判断一下是否已经下载。若重复下载，会报错误码414。
     * 错误码414可能是重复下载，或者下载参数错误
     *
     * @param message
     * @return
     */
    public static boolean isOriginImageHasDownloaded(final IMMessage message) {
        if (message.getAttachStatus() == AttachStatusEnum.transferred) {
            if (message.getAttachment() instanceof FileAttachment) {
                if (!TextUtils.isEmpty(((FileAttachment) message.getAttachment()).getPath())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 正常情况收到消息后附件会自动下载。如果下载失败，可调用该接口重新下载
     *
     * @param message 附件所在的消息体
     * @param thumb   下载缩略图还是原文件。为true时，仅下载缩略图。<br>
     *                该参数仅对图片和视频类消息有效
     * @return AbortableFuture 调用跟踪。可设置回调函数，可中止下载操作
     */
    public static AbortableFuture downloadAttachment(IMMessage message, boolean thumb) {
        return NIMClient.getService(MsgService.class).downloadAttachment(message, true);
    }

    /**
     * 清空消息数据库的所有消息记录。 <br>
     * 可选择是否要同时清空最近联系人列表数据库。<br>
     * 若最近联系人列表也被清空，会触发{@link MsgServiceObserve#observeRecentContactDeleted(Observer, boolean)}通知
     *
     * @param clearRecent 若为true，将同时清空最近联系人列表数据
     */
    public static void clearMsgDatabase(boolean clearRecent) {
        NIMClient.getService(MsgService.class).clearMsgDatabase(clearRecent);
    }

    /**
     * 消息撤回
     *
     * @param message 待撤回的消息
     * @return InvocationFuture 可设置回调函数，监听发送结果。
     */
    public static InvocationFuture<Void> revokeMessage(IMMessage message, RequestCallback<Void> callback) {
        InvocationFuture<Void> voidInvocationFuture = NIMClient.getService(MsgService.class)
                .revokeMessage(message);
        voidInvocationFuture.setCallback(callback);
        return voidInvocationFuture;
    }

    /**
     * 判断是否是当前会话的消息
     *
     * @param message     要判断的消息
     * @param sessionId   会话ID
     * @param sessionType 当前会话类型（单聊、群聊）  @return
     */
    public static boolean isCurrentSessionMessage(IMMessage message, String sessionId, SessionTypeEnum sessionType) {
        return message.getSessionType() == sessionType
                && message.getSessionId() != null
                && message.getSessionId().equals(sessionId);
    }

    /**
     * 获取视频mediaPlayer
     *
     * @param file 视频文件
     * @return mediaPlayer
     */
    private static MediaPlayer getVideoMediaPlayer(File file) {
        try {
            return MediaPlayer.create(UIUtils.getContext(), Uri.parse("file://" + file.getAbsolutePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
