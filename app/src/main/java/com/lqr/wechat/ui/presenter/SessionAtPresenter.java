package com.lqr.wechat.ui.presenter;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqr.audio.AudioPlayManager;
import com.lqr.audio.IAudioPlayListener;
import com.lqr.wechat.R;
import com.lqr.wechat.db.DBManager;
import com.lqr.wechat.db.model.GroupMember;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.model.data.LocationData;
import com.lqr.wechat.model.message.RedPacketMessage;
import com.lqr.wechat.ui.activity.SessionActivity;
import com.lqr.wechat.ui.activity.ShowBigImageActivity;
import com.lqr.wechat.ui.adapter.SessionAdapter;
import com.lqr.wechat.ui.base.BaseFragmentActivity;
import com.lqr.wechat.ui.base.BaseFragmentPresenter;
import com.lqr.wechat.ui.view.ISessionAtView;
import com.lqr.wechat.util.FileOpenUtils;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.MediaFileUtils;
import com.lqr.wechat.util.RedPacketUtil;
import com.lqr.wechat.util.UIUtils;
import com.lqr.wechat.widget.CustomDialog;
import com.yunzhanghu.redpacketsdk.RPSendPacketCallback;
import com.yunzhanghu.redpacketsdk.bean.RedPacketInfo;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.FileMessage;
import io.rong.message.GroupNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.RecallNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class SessionAtPresenter extends BaseFragmentPresenter<ISessionAtView> {

    public Conversation.ConversationType mConversationType;
    private String mSessionId;
    private String mPushCotent = "";//接收方离线时需要显示的push消息内容。
    private String mPushData = "";//接收方离线时需要在push消息中携带的非显示内容。
    private int mMessageCount = 5;//一次获取历史消息的最大数量

    private List<Message> mData = new ArrayList<>();
    private SessionAdapter mAdapter;
    private CustomDialog mSessionMenuDialog;

    public SessionAtPresenter(BaseFragmentActivity context, String sessionId, Conversation.ConversationType conversationType) {
        super(context);
        mSessionId = sessionId;
        mConversationType = conversationType;
    }

    public void loadMessage() {
        loadData();
        setAdapter();
    }

    private void loadData() {
        getLocalHistoryMessage();
        setAdapter();
    }

    public void loadMore() {
        getLocalHistoryMessage();
        mAdapter.notifyDataSetChangedWrapper();
    }

    public void receiveNewMessage(Message message) {
        mData.add(message);
        setAdapter();
        RongIMClient.getInstance().clearMessagesUnreadStatus(mConversationType, mSessionId);
    }

    public void resetDraft() {
        Observable.just(RongIMClient.getInstance().getTextMessageDraft(mConversationType, mSessionId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    if (!TextUtils.isEmpty(s)) {
                        getView().getEtContent().setText(s);
                        RongIMClient.getInstance().clearTextMessageDraft(mConversationType, mSessionId);
                    }
                }, this::loadError);
    }

    public void saveDraft() {
        String draft = getView().getEtContent().getText().toString();
        if (!TextUtils.isEmpty(draft)) {
            RongIMClient.getInstance().saveTextMessageDraft(mConversationType, mSessionId, draft);
        }
    }

    public void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new SessionAdapter(mContext, mData, this);
            mAdapter.setOnItemClickListener((helper, parent, itemView, position) -> {
                Message message = mData.get(position);
                MessageContent content = message.getContent();
                if (content instanceof ImageMessage) {
                    ImageMessage imageMessage = (ImageMessage) content;
                    Intent intent = new Intent(mContext, ShowBigImageActivity.class);
                    intent.putExtra("url", imageMessage.getLocalUri() == null ? imageMessage.getRemoteUri().toString() : imageMessage.getLocalUri().toString());
                    mContext.jumpToActivity(intent);
                } else if (content instanceof FileMessage) {
                    FileMessage fileMessage = (FileMessage) content;
                    if (MediaFileUtils.isVideoFileType(fileMessage.getName())) {
                        helper.getView(R.id.bivPic).setOnClickListener(v -> {
                            boolean isSend = message.getMessageDirection() == Message.MessageDirection.SEND ? true : false;
                            if (isSend) {
                                if (fileMessage.getLocalPath() != null && new File(fileMessage.getLocalPath().getPath()).exists()) {
                                    FileOpenUtils.openFile(mContext, fileMessage.getLocalPath().getPath());
                                } else {
                                    downloadMediaMessage(message);
                                }
                            } else {
                                Message.ReceivedStatus receivedStatus = message.getReceivedStatus();
                                if (receivedStatus.isDownload() || receivedStatus.isRetrieved()) {
                                    if (fileMessage.getLocalPath() != null) {
                                        FileOpenUtils.openFile(mContext, fileMessage.getLocalPath().getPath());
                                    } else {
                                        UIUtils.showToast(UIUtils.getString(R.string.file_out_of_date));
                                    }
                                } else {
                                    downloadMediaMessage(message);
                                }
                            }
                        });
                    }
                } else if (content instanceof VoiceMessage) {
                    VoiceMessage voiceMessage = (VoiceMessage) content;
                    final ImageView ivAudio = helper.getView(R.id.ivAudio);
                    AudioPlayManager.getInstance().startPlay(mContext, voiceMessage.getUri(), new IAudioPlayListener() {
                        @Override
                        public void onStart(Uri var1) {
                            if (ivAudio != null && ivAudio.getBackground() instanceof AnimationDrawable) {
                                AnimationDrawable animation = (AnimationDrawable) ivAudio.getBackground();
                                animation.start();
                            }
                        }

                        @Override
                        public void onStop(Uri var1) {
                            if (ivAudio != null && ivAudio.getBackground() instanceof AnimationDrawable) {
                                AnimationDrawable animation = (AnimationDrawable) ivAudio.getBackground();
                                animation.stop();
                                animation.selectDrawable(0);
                            }

                        }

                        @Override
                        public void onComplete(Uri var1) {
                            if (ivAudio != null && ivAudio.getBackground() instanceof AnimationDrawable) {
                                AnimationDrawable animation = (AnimationDrawable) ivAudio.getBackground();
                                animation.stop();
                                animation.selectDrawable(0);
                            }
                        }
                    });
                } else if (content instanceof RedPacketMessage) {
                    RedPacketMessage redPacketMessage = (RedPacketMessage) content;
                    int chatType = mConversationType == Conversation.ConversationType.PRIVATE ? RPConstant.RP_ITEM_TYPE_SINGLE : RPConstant.RP_ITEM_TYPE_GROUP;
                    String redPacketId = redPacketMessage.getBribery_ID();
                    String redPacketType = redPacketMessage.getBribery_Message();
                    String receiverId = UserCache.getId();
                    String direct = RPConstant.MESSAGE_DIRECT_RECEIVE;
                    RedPacketUtil.openRedPacket(((SessionActivity) mContext), chatType, redPacketId, redPacketType, receiverId, direct);
                }
            });
            getView().getRvMsg().setAdapter(mAdapter);
            mAdapter.setOnItemLongClickListener((helper, viewGroup, view, position) -> {
                View sessionMenuView = View.inflate(mContext, R.layout.dialog_session_menu, null);
                mSessionMenuDialog = new CustomDialog(mContext, sessionMenuView, R.style.MyDialog);
                TextView tvReCall = (TextView) sessionMenuView.findViewById(R.id.tvReCall);
                TextView tvDelete = (TextView) sessionMenuView.findViewById(R.id.tvDelete);

                //根据消息类型控制显隐
                Message message = mData.get(position);
                MessageContent content = message.getContent();
                if (content instanceof GroupNotificationMessage || content instanceof RecallNotificationMessage) {
                    return false;
                }
                if (content instanceof RedPacketMessage || !message.getSenderUserId().equalsIgnoreCase(UserCache.getId())) {
                    tvReCall.setVisibility(View.GONE);
                }

                tvReCall.setOnClickListener(v -> RongIMClient.getInstance().recallMessage(message, "", new RongIMClient.ResultCallback<RecallNotificationMessage>() {
                    @Override
                    public void onSuccess(RecallNotificationMessage recallNotificationMessage) {
                        UIUtils.postTaskSafely(() -> {
                            recallMessageAndInsertMessage(recallNotificationMessage, position);
                            mSessionMenuDialog.dismiss();
                            mSessionMenuDialog = null;
                            UIUtils.showToast(UIUtils.getString(R.string.recall_success));
                        });
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        UIUtils.postTaskSafely(() -> {
                            mSessionMenuDialog.dismiss();
                            mSessionMenuDialog = null;
                            UIUtils.showToast(UIUtils.getString(R.string.recall_fail) + ":" + errorCode.getValue());
                        });
                    }
                }));
                tvDelete.setOnClickListener(v -> RongIMClient.getInstance().deleteMessages(new int[]{message.getMessageId()}, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        UIUtils.postTaskSafely(() -> {
                            mSessionMenuDialog.dismiss();
                            mSessionMenuDialog = null;
                            mData.remove(position);
                            mAdapter.notifyDataSetChangedWrapper();
                            UIUtils.showToast(UIUtils.getString(R.string.delete_success));
                        });
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        UIUtils.postTaskSafely(() -> {
                            mSessionMenuDialog.dismiss();
                            mSessionMenuDialog = null;
                            UIUtils.showToast(UIUtils.getString(R.string.delete_fail) + ":" + errorCode.getValue());
                        });
                    }
                }));
                mSessionMenuDialog.show();
                return false;
            });
            UIUtils.postTaskDelay(() -> getView().getRvMsg().smoothMoveToPosition(mData.size() - 1), 200);
        } else {
            mAdapter.notifyDataSetChangedWrapper();
            if (getView() != null && getView().getRvMsg() != null)
                rvMoveToBottom();
        }
    }

    private void rvMoveToBottom() {
        getView().getRvMsg().smoothMoveToPosition(mData.size() - 1);
    }

    private void updateMessageStatus(Message message) {
        for (int i = 0; i < mData.size(); i++) {
            Message msg = mData.get(i);
            if (msg.getMessageId() == message.getMessageId()) {
                mData.remove(i);
                mData.add(i, message);
                mAdapter.notifyDataSetChangedWrapper();
                break;
            }
        }
    }

    private void updateMessageStatus(int messageId) {
        RongIMClient.getInstance().getMessage(messageId, new RongIMClient.ResultCallback<Message>() {
            @Override
            public void onSuccess(Message message) {
                for (int i = 0; i < mData.size(); i++) {
                    Message msg = mData.get(i);
                    if (msg.getMessageId() == message.getMessageId()) {
                        mData.remove(i);
                        mData.add(i, message);
                        mAdapter.notifyDataSetChangedWrapper();
                        break;
                    }
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    public void sendTextMsg() {
        sendTextMsg(getView().getEtContent().getText().toString());
        getView().getEtContent().setText("");
    }

    public void sendTextMsg(String content) {
        RongIMClient.getInstance().sendMessage(mConversationType, mSessionId, TextMessage.obtain(content), mPushCotent, mPushData,
                new RongIMClient.SendMessageCallback() {// 发送消息的回调
                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                        updateMessageStatus(integer);
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        updateMessageStatus(integer);
                    }
                }, new RongIMClient.ResultCallback<Message>() {//消息存库的回调，可用于获取消息实体
                    @Override
                    public void onSuccess(Message message) {
                        mAdapter.addLastItem(message);
                        rvMoveToBottom();
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
    }

    public void sendImgMsg(Uri imageFileThumbUri, Uri imageFileSourceUri) {
        ImageMessage imgMsg = ImageMessage.obtain(imageFileThumbUri, imageFileSourceUri);
        RongIMClient.getInstance().sendImageMessage(mConversationType, mSessionId, imgMsg, mPushCotent, mPushData,
                new RongIMClient.SendImageMessageCallback() {
                    @Override
                    public void onAttached(Message message) {
                        //保存数据库成功
                        mAdapter.addLastItem(message);
                        rvMoveToBottom();
                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                        //发送失败
                        updateMessageStatus(message);
                    }

                    @Override
                    public void onSuccess(Message message) {
                        //发送成功
                        updateMessageStatus(message);
                    }

                    @Override
                    public void onProgress(Message message, int progress) {
                        //发送进度
                        message.setExtra(progress + "");
                        updateMessageStatus(message);
                    }
                });
    }

    public void sendImgMsg(File imageFileThumb, File imageFileSource) {
        Uri imageFileThumbUri = Uri.fromFile(imageFileThumb);
        Uri imageFileSourceUri = Uri.fromFile(imageFileSource);
        sendImgMsg(imageFileThumbUri, imageFileSourceUri);
    }

    public void sendFileMsg(File file) {
        Message fileMessage = Message.obtain(mSessionId, mConversationType, FileMessage.obtain(Uri.fromFile(file)));
        RongIMClient.getInstance().sendMediaMessage(fileMessage, mPushCotent, mPushData, new IRongCallback.ISendMediaMessageCallback() {
            @Override
            public void onProgress(Message message, int progress) {
                //发送进度
                message.setExtra(progress + "");
                updateMessageStatus(message);
            }

            @Override
            public void onCanceled(Message message) {

            }

            @Override
            public void onAttached(Message message) {
                //保存数据库成功
                mAdapter.addLastItem(message);
                rvMoveToBottom();
            }

            @Override
            public void onSuccess(Message message) {
                //发送成功
                updateMessageStatus(message);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                //发送失败
                updateMessageStatus(message);
            }
        });
    }

    public void sendLocationMessage(LocationData locationData) {
        LocationMessage message = LocationMessage.obtain(locationData.getLat(), locationData.getLng(), locationData.getPoi(), Uri.parse(locationData.getImgUrl()));
        RongIMClient.getInstance().sendLocationMessage(Message.obtain(mSessionId, mConversationType, message), mPushCotent, mPushData, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {
                //保存数据库成功
                mAdapter.addLastItem(message);
                rvMoveToBottom();
            }

            @Override
            public void onSuccess(Message message) {
                //发送成功
                updateMessageStatus(message);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                //发送失败
                updateMessageStatus(message);
            }
        });
    }

    public void sendAudioFile(Uri audioPath, int duration) {
        if (audioPath != null) {
            File file = new File(audioPath.getPath());
            if (!file.exists() || file.length() == 0L) {
                LogUtils.sf(UIUtils.getString(R.string.send_audio_fail));
                return;
            }
            VoiceMessage voiceMessage = VoiceMessage.obtain(audioPath, duration);
            RongIMClient.getInstance().sendMessage(Message.obtain(mSessionId, mConversationType, voiceMessage), mPushCotent, mPushData, new IRongCallback.ISendMessageCallback() {
                @Override
                public void onAttached(Message message) {
                    //保存数据库成功
                    mAdapter.addLastItem(message);
                    rvMoveToBottom();
                }

                @Override
                public void onSuccess(Message message) {
                    //发送成功
                    updateMessageStatus(message);
                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    //发送失败
                    updateMessageStatus(message);
                }
            });
        }
    }

    public void sendRedPacketMsg() {
        if (mConversationType == Conversation.ConversationType.PRIVATE) {
            UserInfo userInfo = DBManager.getInstance().getUserInfo(mSessionId);
            if (userInfo != null)
                RedPacketUtil.startRedPacket(mContext, userInfo, RPSendPacketCallback);
        } else {
            List<GroupMember> groupMembers = DBManager.getInstance().getGroupMembers(mSessionId);
            if (groupMembers != null)
                RedPacketUtil.startRedPacket(mContext, mSessionId, groupMembers.size(), RPSendPacketCallback);
        }
    }

    RPSendPacketCallback RPSendPacketCallback = new RPSendPacketCallback() {
        @Override
        public void onGenerateRedPacketId(String redPacketId) {

        }

        @Override
        public void onSendPacketSuccess(RedPacketInfo redPacketInfo) {
            RedPacketMessage rpMsg = RedPacketMessage.obtain(redPacketInfo.redPacketId, redPacketInfo.fromNickName, redPacketInfo.redPacketType, redPacketInfo.redPacketGreeting);
            RongIMClient.getInstance().sendMessage(Message.obtain(mSessionId, mConversationType, rpMsg), mPushCotent, mPushData, new IRongCallback.ISendMessageCallback() {
                @Override
                public void onAttached(Message message) {
                    //保存数据库成功
                    mAdapter.addLastItem(message);
                    rvMoveToBottom();
                }

                @Override
                public void onSuccess(Message message) {
                    //发送成功
                    updateMessageStatus(message);
                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    //发送失败
                    updateMessageStatus(message);
                }
            });
        }
    };

    public void downloadMediaMessage(Message message) {
        RongIMClient.getInstance().downloadMediaMessage(message, new IRongCallback.IDownloadMediaMessageCallback() {
            @Override
            public void onSuccess(Message message) {
                message.getReceivedStatus().setDownload();
                updateMessageStatus(message);
            }

            @Override
            public void onProgress(Message message, int progress) {
                //发送进度
                message.setExtra(progress + "");
                updateMessageStatus(message);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                updateMessageStatus(message);
            }

            @Override
            public void onCanceled(Message message) {
                updateMessageStatus(message);
            }
        });
    }


    //获取会话中，从指定消息之前、指定数量的最新消息实体
    public void getLocalHistoryMessage() {
        //没有消息第一次调用应设置为:-1。
        int messageId = -1;
        if (mData.size() > 0) {
            messageId = mData.get(0).getMessageId();
        } else {
            messageId = -1;
        }

        RongIMClient.getInstance().getHistoryMessages(mConversationType, mSessionId, messageId, mMessageCount, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                getView().getRefreshLayout().endRefreshing();
                if (messages == null || messages.size() == 0)
                    getRemoteHistoryMessages();
                else
                    saveHistoryMsg(messages);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                getView().getRefreshLayout().endRefreshing();
                loadMessageError(errorCode);
            }
        });
    }

    //单聊、群聊、讨论组、客服的历史消息从远端获取
    public void getRemoteHistoryMessages() {
        //消息中的 sentTime；第一次可传 0，获取最新 count 条。
        long dateTime = 0;
        if (mData.size() > 0) {
            dateTime = mData.get(0).getSentTime();
        } else {
            dateTime = 0;
        }

        RongIMClient.getInstance().getRemoteHistoryMessages(mConversationType, mSessionId, dateTime, mMessageCount,
                new RongIMClient.ResultCallback<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> messages) {
                        saveHistoryMsg(messages);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        loadMessageError(errorCode);
                    }
                });
    }

    private void saveHistoryMsg(List<Message> messages) {
        //messages的时间顺序从新到旧排列，所以必须反过来加入到mData中
        if (messages != null && messages.size() > 0) {
            for (Message msg : messages) {
                mData.add(0, msg);
            }
            getView().getRvMsg().moveToPosition(messages.size() - 1);
        }
    }

    private void loadMessageError(RongIMClient.ErrorCode errorCode) {
        LogUtils.sf("拉取历史消息失败，errorCode = " + errorCode);
    }

    private void loadError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
    }

    public void recallMessageFromListener(int messageId, RecallNotificationMessage recallNotificationMessage) {
        for (int i = 0; i < mData.size(); i++) {
            Message message = mData.get(i);
            if (message.getMessageId() == messageId) {
                recallMessageAndInsertMessage(recallNotificationMessage, i);
                break;
            }
        }
    }

    private void recallMessageAndInsertMessage(RecallNotificationMessage recallNotificationMessage, int position) {
        RongIMClient.getInstance().insertMessage(mConversationType, mSessionId, UserCache.getId(), recallNotificationMessage);
        mData.remove(position);
        mData.add(Message.obtain(mSessionId, mConversationType, recallNotificationMessage));
        mAdapter.notifyDataSetChangedWrapper();
    }
}
