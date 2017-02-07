package com.lqr.wechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.emoji.MoonUtil;
import com.lqr.emoji.StickerManager;
import com.lqr.wechat.R;
import com.lqr.wechat.activity.FilePreviewActivity;
import com.lqr.wechat.activity.ImageWatchActivity;
import com.lqr.wechat.activity.SessionActivity;
import com.lqr.wechat.imageloader.ImageLoaderManager;
import com.lqr.wechat.nimsdk.NimMessageSDK;
import com.lqr.wechat.nimsdk.NimTeamSDK;
import com.lqr.wechat.nimsdk.NimUserInfoSDK;
import com.lqr.wechat.nimsdk.audio.BaseAudioControl;
import com.lqr.wechat.nimsdk.audio.MessageAudioControl;
import com.lqr.wechat.nimsdk.audio.Playable;
import com.lqr.wechat.nimsdk.custom.StickerAttachment;
import com.lqr.wechat.nimsdk.utils.ImageUtil;
import com.lqr.wechat.nimsdk.utils.ScreenUtil;
import com.lqr.wechat.utils.Bimp;
import com.lqr.wechat.utils.FileIconUtils;
import com.lqr.wechat.utils.FileOpenUtils;
import com.lqr.wechat.utils.FileUtils;
import com.lqr.wechat.utils.LogUtils;
import com.lqr.wechat.utils.MimeTypeUtils;
import com.lqr.wechat.utils.TimeUtils;
import com.lqr.wechat.utils.UIUtils;
import com.lqr.wechat.utils.VideoThumbLoader;
import com.lqr.wechat.view.BubbleImageView;
import com.lqr.wechat.view.CircularProgressBar;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.media.record.AudioRecorder;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.MemberChangeAttachment;
import com.netease.nimlib.sdk.team.model.MuteMemberAttachment;
import com.netease.nimlib.sdk.team.model.UpdateTeamAttachment;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.netease.nimlib.sdk.msg.constant.MsgTypeEnum.notification;

/**
 * @创建者 CSDN_LQR
 * @描述 会话列表适配器
 */
public class SessionAdapter extends LQRAdapterForRecyclerView<IMMessage> {

    public static final int CLICK_TO_PLAY_AUDIO_DELAY = 500;
    private Context mContext;

    private static final int NOTIFICATION = R.layout.item_notification;
    private static final int SEND_TEXT = R.layout.item_text_send;
    private static final int RECEIVE_TEXT = R.layout.item_text_receive;
    private static final int SEND_STICKER = R.layout.item_sticker_send;
    private static final int RECEIVE_STICKER = R.layout.item_sticker_receive;
    private static final int SEND_IMAGE = R.layout.item_image_send;
    private static final int RECEIVE_IMAGE = R.layout.item_image_receive;
    private static final int SEND_VIDEO = R.layout.item_video_send;
    private static final int RECEIVE_VIDEO = R.layout.item_video_receive;
    private static final int SEND_LOCATION = R.layout.item_location_send;
    private static final int RECEIVE_LOCATION = R.layout.item_location_receive;
    private static final int SEND_AUDIO = R.layout.item_audio_send;
    private static final int RECEIVE_AUDIO = R.layout.item_audio_receive;
    private static final int SEND_FILE = R.layout.item_file_send;
    private static final int RECEIVE_FILE = R.layout.item_file_receive;

    private Map<String, Float> mProgress = new HashMap<>();
    private MessageAudioControl mAudioControl;
    private ImageView mAnimationView;

    public SessionAdapter(Context context, List<IMMessage> data) {
        super(context, data);
        mContext = context;
        mAudioControl = MessageAudioControl.getInstance(mContext);
    }

    public SessionAdapter(Context context, int defaultLayoutId, List<IMMessage> data) {
        super(context, defaultLayoutId, data);
    }

    @Override
    public void convert(LQRViewHolderForRecyclerView helper, final IMMessage item, final int position) {

        //设置时间
        setTime(helper, item, position);

        if (item.getMsgType() != notification) {
            //设置头像
            setHeader(helper, item);

            //显示昵称
            if (item.getSessionType() == SessionTypeEnum.Team) {
                helper.setViewVisibility(R.id.tvName, NimTeamSDK.shouldShowNickName(item.getSessionId()) ? View.VISIBLE : View.GONE)
                        .setText(R.id.tvName, NimTeamSDK.getTeamMemberDisplayNameWithoutMe(item.getSessionId(), item.getFromAccount()));
            } else {
                helper.setViewVisibility(R.id.tvName, View.GONE);
            }

            //点击重发
            helper.getView(R.id.ivError).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NimMessageSDK.reSendMessage(item);
//                notifyItemChanged(position);
//                notifyDataSetChanged();
                    ((SessionActivity) mContext).initData();
                }
            });

            //更新消息发送状态
            setViewWithStatus(helper, item, position);
        }

        //文本消息
        if (item.getMsgType() == MsgTypeEnum.text) {
            setTextMessage(helper, item);
        }
        //贴图消息
        else if (item.getMsgType() == MsgTypeEnum.custom) {
            setStickerMessage(helper, item);
        }
        //图片消息
        else if (item.getMsgType() == MsgTypeEnum.image) {
            setImageMessage(helper, item);
        }
        //语音消息
        else if (item.getMsgType() == MsgTypeEnum.audio) {
            setAudioMessage(helper, item);
        }
        //视频消息
        else if (item.getMsgType() == MsgTypeEnum.video) {
            setVideoMessage(helper, item, position);
        }
        //文件消息
        else if (item.getMsgType() == MsgTypeEnum.file) {
            setFileMessage(helper, item);
        }
        //通知类消息
        else if (item.getMsgType() == notification) {
            setNotificationMessage(helper, item);
        }

    }

    private void setTime(LQRViewHolderForRecyclerView helper, IMMessage item, int position) {
        if (position > 0) {
            IMMessage preMessage = getData().get(position - 1);
            if (item.getTime() - preMessage.getTime() > (5 * 60 * 1000)) {//与上一条数据相关5分钟则显示时间
                helper.setViewVisibility(R.id.tvTime, View.VISIBLE).setText(R.id.tvTime, TimeUtils.getMsgFormatTime(item.getTime()));
            } else {
                helper.setViewVisibility(R.id.tvTime, View.GONE);
            }
        } else {
            helper.setViewVisibility(R.id.tvTime, View.VISIBLE).setText(R.id.tvTime, TimeUtils.getMsgFormatTime(item.getTime()));
        }
    }

    private void setHeader(LQRViewHolderForRecyclerView helper, IMMessage item) {
        ImageView ivAvatar = helper.getView(R.id.ivAvatar);
        String avatar = NimUserInfoSDK.getUser(item.getFromAccount()).getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            ImageLoaderManager.LoadNetImage(avatar, ivAvatar);
        } else {
            ivAvatar.setImageResource(R.mipmap.default_header);
        }
    }

    private void setTextMessage(LQRViewHolderForRecyclerView helper, IMMessage item) {
        helper.setText(R.id.tvText, item.getContent());
        //识别并显示表情
        MoonUtil.identifyFaceExpression(UIUtils.getContext(), helper.getView(R.id.tvText), item.getContent(), ImageSpan.ALIGN_BOTTOM);
    }

    private void setStickerMessage(LQRViewHolderForRecyclerView helper, IMMessage item) {
        StickerAttachment attachment = (StickerAttachment) item.getAttachment();
        String uri = StickerManager.getInstance().getStickerBitmapUri(attachment.getCatalog(), attachment.getChartlet());
        ImageLoaderManager.LoadNetImage(uri, (ImageView) helper.getView(R.id.ivSticker));
    }

    private void setImageMessage(LQRViewHolderForRecyclerView helper, final IMMessage item) {
        final BubbleImageView bivPic = helper.getView(R.id.bivPic);
        final ImageAttachment ia = (ImageAttachment) item.getAttachment();

        if (!TextUtils.isEmpty(ia.getPath())) {
            ImageLoaderManager.LoadLocalImage(ia.getPath(), bivPic);
        } else {
            //判断本地是否有缩略图
            if (ia.getThumbPath() == null) {
                LogUtils.sf("本地没有缩略图");
                AbortableFuture abortableFuture = NimMessageSDK.downloadAttachment(item, true);
                abortableFuture.setCallback(new RequestCallback() {
                    @Override
                    public void onSuccess(Object param) {
                        Bitmap bitmap = Bimp.getLoacalBitmap(ia.getThumbPath());
                        if (bitmap != null) {
                            bivPic.setImageBitmap(bitmap);
                        }
                    }

                    @Override
                    public void onFailed(int code) {

                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
            } else {
                LogUtils.sf("本地有缩略图");
                Bitmap bitmap = Bimp.getLoacalBitmap(ia.getThumbPath());
                if (bitmap != null) {
                    bivPic.setImageBitmap(bitmap);
                }
            }
        }

        //查看图片大图
        bivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageWatchActivity.class);
                intent.putExtra("message", item);
                intent.putExtra("account", ((SessionActivity) mContext).mSessionId);
                intent.putExtra("sessionType", ((SessionActivity) mContext).mSessionType);

                mContext.startActivity(intent);
            }
        });
    }

    /**
     * 设置语音信息
     */
    private void setAudioMessage(final LQRViewHolderForRecyclerView helper, final IMMessage item) {
        final AudioAttachment aa = (AudioAttachment) item.getAttachment();
        long durationMillis = aa.getDuration();
        long durationSecond = durationMillis / 1000;
        int increment = (int) (ScreenUtil.getDisplayWidth() / 2 / AudioRecorder.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND * durationSecond);

        //设置语音的长度
        RelativeLayout rlAudio = helper.setText(R.id.tvDuration, durationSecond + "''").getView(R.id.rlAudio);
        ViewGroup.LayoutParams params = rlAudio.getLayoutParams();
        params.width = UIUtils.dip2Px(65) + UIUtils.dip2Px(increment);
        rlAudio.setLayoutParams(params);

        //设置条目点击事件
        helper.getView(R.id.rlAudio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放当前播放的其他语音
                mAudioControl.stopAudio();
                //得到当前要播放语音的动画控件
                mAnimationView = helper.getView(R.id.ivAudio);
                //下载音频
                if (TextUtils.isEmpty(aa.getPath())) {
                    OkHttpUtils.get().url(aa.getUrl()).build().execute(new FileCallBack(FileUtils.getDirFromPath(aa.getPathForSave()), FileUtils.getFileNameFromPath(aa.getPathForSave())) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            UIUtils.showToast("语音下载失败");
                        }

                        @Override
                        public void onResponse(File response, int id) {
                            playAudioDelayAndSetPlayNext(item);
                        }
                    });
                } else {
                    playAudioDelayAndSetPlayNext(item);
                }

            }
        });
    }

    /**
     * 设置视频信息
     */
    private void setVideoMessage(LQRViewHolderForRecyclerView helper, final IMMessage item, final int position) {
        final BubbleImageView bivPic = helper.getView(R.id.bivPic);
        final VideoAttachment va = (VideoAttachment) item.getAttachment();

        //设置控件的尺寸
        int[] bounds = new int[]{va.getWidth(), va.getHeight()};
        final ImageUtil.ImageSize imageSize = ImageUtil.getThumbnailDisplaySize(bounds[0], bounds[1], getImageMaxEdge(), getImageMinEdge());
        setLayoutParams(imageSize.width, imageSize.height, bivPic);

        //视频文件存在
        if (!TextUtils.isEmpty(va.getPath())) {
            VideoThumbLoader.getInstance().showThumb(va.getPath(), bivPic, imageSize.width, imageSize.height);
        } else {
            bivPic.setImageResource(R.mipmap.img_video_default);
            //下载视频缩略图
            AbortableFuture abortableFuture = NimMessageSDK.downloadAttachment(item, true);
            abortableFuture.setCallback(new RequestCallback() {
                @Override
                public void onSuccess(Object param) {
                    Bitmap bitmap = Bimp.getLoacalBitmap(va.getThumbPath());
                    if (bitmap != null) {
                        bivPic.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onFailed(int code) {

                }

                @Override
                public void onException(Throwable exception) {

                }
            });
        }

        //点击事件
        helper.getView(R.id.ivPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //视频文件存在
                if (!TextUtils.isEmpty(va.getPath())) {
                    //播放视频
                    FileOpenUtils.openFile(mContext, va.getPath(), MimeTypeUtils.getMimeType(va.getDisplayName()));
                } else {
                    //下载视频文件
                    OkHttpUtils.get().url(va.getUrl()).build().execute(new FileCallBack(FileUtils.getDirFromPath(va.getPathForSave()), FileUtils.getFileNameFromPath(va.getPathForSave())) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            UIUtils.showToast("下载视频失败");
                        }

                        @Override
                        public void onResponse(File response, int id) {
                            if (!TextUtils.isEmpty(va.getPath())) {
                                VideoThumbLoader.getInstance().showThumb(va.getPath(), bivPic, imageSize.width, imageSize.height);
                                notifyItemChanged(position);
                            }
                        }

                        @Override
                        public void inProgress(float progress, long total, int id) {//use progress: 0 ~ 1
                            progress = progress * 100;
                            putProgress(item, progress);
                            if (progress == 0) {
                                item.setAttachStatus(AttachStatusEnum.def);
                            } else if (progress < 100) {
                                item.setAttachStatus(AttachStatusEnum.transferring);
                            } else {
                                item.setAttachStatus(AttachStatusEnum.transferred);
                            }
                            notifyItemChanged(position);
                            super.inProgress(progress, total, id);
                        }
                    });
                }
            }
        });
    }

    /**
     * 设置文件信息
     */
    private void setFileMessage(LQRViewHolderForRecyclerView helper, final IMMessage item) {
        FileAttachment fa = (FileAttachment) item.getAttachment();
        helper.setImageResource(R.id.ivIcon, FileIconUtils.getFileIconResId(fa.getExtension()))
                .setText(R.id.tvFileName, fa.getFileName())
                .setText(R.id.tvFileSize, FileUtils.formateFileSize(fa.getSize()))
                .getView(R.id.llFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到文件预览界面下载文件
                Intent intent = new Intent(mContext, FilePreviewActivity.class);
                intent.putExtra("message", item);
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * 设置通知类信息
     */
    private void setNotificationMessage(LQRViewHolderForRecyclerView helper, IMMessage item) {
        NotificationAttachment na = (NotificationAttachment) item.getAttachment();
        String fromAccount = item.getFromAccount();
        String text = "";
        switch (na.getType()) {
            case InviteMember:
                text = NimTeamSDK.buildInviteMemberNotification(((MemberChangeAttachment) na), item.getSessionId(), fromAccount);
                break;
            case KickMember:
                text = NimTeamSDK.buildKickMemberNotification(((MemberChangeAttachment) na), item.getSessionId(), fromAccount);
                break;
            case LeaveTeam:
                text = NimTeamSDK.buildLeaveTeamNotification(item.getSessionId(), fromAccount);
                break;
            case DismissTeam:
                text = NimTeamSDK.buildDismissTeamNotification(item.getSessionId(), fromAccount);
                break;
            case UpdateTeam:
                text = NimTeamSDK.buildUpdateTeamNotification(((UpdateTeamAttachment) na), item.getSessionId(), fromAccount);
                break;
            case PassTeamApply:
                text = NimTeamSDK.buildManagerPassTeamApplyNotification((MemberChangeAttachment) na, item.getSessionId());
                break;
            case TransferOwner:
                text = NimTeamSDK.buildTransferOwnerNotification(((MemberChangeAttachment) na), item.getSessionId(), fromAccount);
                break;
            case AddTeamManager:
                text = NimTeamSDK.buildAddTeamManagerNotification((MemberChangeAttachment) na, item.getSessionId());
                break;
            case RemoveTeamManager:
                text = NimTeamSDK.buildRemoveTeamManagerNotification((MemberChangeAttachment) na, item.getSessionId());
                break;
            case AcceptInvite:
                text = NimTeamSDK.buildAcceptInviteNotification(((MemberChangeAttachment) na), item.getSessionId(), fromAccount);
                break;
            case MuteTeamMember:
                text = NimTeamSDK.buildMuteTeamNotification((MuteMemberAttachment) na, item.getSessionId());
                break;
            default:
                text = NimTeamSDK.getTeamMemberDisplayNameWithYou(item.getSessionId(), fromAccount) + ": unknown message";
                break;
        }
        helper.setText(R.id.tvNotification, text);
    }

    private void setViewWithStatus(LQRViewHolderForRecyclerView helper, IMMessage item, final int position) {
        //消息发送/接收状态
        MsgStatusEnum status = item.getStatus();

        if (status == MsgStatusEnum.success) {
            LogUtils.sf("发送成功...");
            helper.setViewVisibility(R.id.llError, View.GONE);
            setProgressVisiable(helper, item, false);
        } else if (status == MsgStatusEnum.fail) {
            LogUtils.sf("发送失败...");
            helper.setViewVisibility(R.id.llError, View.VISIBLE);
            setProgressVisiable(helper, item, false);
        } else if (status == MsgStatusEnum.sending) {
            LogUtils.sf("发送中...");
            helper.setViewVisibility(R.id.llError, View.GONE);
            setProgressVisiable(helper, item, true);
            //更新进度
            updateProgress(helper, item);

            //因为网易云集的状态监听不会监听到success，所以这里手动再刷新一次
            UIUtils.postTaskDelay(new Runnable() {
                @Override
                public void run() {
                    notifyItemChanged(position);
                }
            }, 1000);
        }

        //附件发送/接收状态
        if (item.getAttachment() != null) {
            AttachStatusEnum attachStatus = item.getAttachStatus();
            if (attachStatus == AttachStatusEnum.def) {

            } else if (attachStatus == AttachStatusEnum.transferring) {
                setProgressVisiable(helper, item, true);
                if (item.getMsgType() == MsgTypeEnum.video)
                    helper.setViewVisibility(R.id.ivPlay, View.GONE);
                //更新进度
                updateProgress(helper, item);
            } else if (attachStatus == AttachStatusEnum.transferred) {
                setProgressVisiable(helper, item, false);
                if (item.getMsgType() == MsgTypeEnum.video)
                    helper.setViewVisibility(R.id.ivPlay, View.VISIBLE);
            } else if (attachStatus == AttachStatusEnum.fail) {
                setProgressVisiable(helper, item, false);
                if (item.getMsgType() == MsgTypeEnum.video)
                    helper.setViewVisibility(R.id.ivPlay, View.GONE);
            }
        }
    }

    private void updateProgress(LQRViewHolderForRecyclerView helper, IMMessage item) {
        if (item.getAttachment() instanceof ImageAttachment) {
            //更新进度
            BubbleImageView bivPic = helper.getView(R.id.bivPic);
            Float progress = getProgress(item);
            if (progress != null) {
                bivPic.setPercent((int) progress.floatValue());
                LogUtils.sf("图片进度：" + getProgress(item) + "%");
            }
        } else if (item.getAttachment() instanceof VideoAttachment) {
            //更新进度
            CircularProgressBar cpbLoading = helper.getView(R.id.cpbLoading);
            Float progress = getProgress(item);
            if (progress != null) {
                cpbLoading.setProgress(progress.intValue());
                LogUtils.sf("视频进度：" + getProgress(item) + "%");
            }
        }
    }

    private void setProgressVisiable(LQRViewHolderForRecyclerView helper, IMMessage item, boolean visiable) {
        if (visiable) {
            if (item.getMsgType() == MsgTypeEnum.text || item.getMsgType() == MsgTypeEnum.custom || item.getMsgType() == MsgTypeEnum.location) {
                helper.setViewVisibility(R.id.pbSending, View.VISIBLE);
            } else if (item.getMsgType() == MsgTypeEnum.image || item.getMsgType() == MsgTypeEnum.video) {
                BubbleImageView bivPic = helper.getView(R.id.bivPic);
                bivPic.showShadow(true);
                if (item.getMsgType() == MsgTypeEnum.image)
                    bivPic.setProgressVisible(true);
                else
                    helper.setViewVisibility(R.id.cpbLoading, View.VISIBLE).setViewVisibility(R.id.ivPlay, View.GONE);
            }
        } else {
            if (item.getMsgType() == MsgTypeEnum.text || item.getMsgType() == MsgTypeEnum.custom || item.getMsgType() == MsgTypeEnum.location) {
                helper.setViewVisibility(R.id.pbSending, View.GONE);
            } else if (item.getMsgType() == MsgTypeEnum.image || item.getMsgType() == MsgTypeEnum.video) {
                BubbleImageView bivPic = helper.getView(R.id.bivPic);
                bivPic.showShadow(false);
                if (item.getMsgType() == MsgTypeEnum.image)
                    bivPic.setProgressVisible(false);
                else
                    helper.setViewVisibility(R.id.cpbLoading, View.GONE).setViewVisibility(R.id.ivPlay, View.VISIBLE);
            }
        }
    }

    /**
     * 添加条目进度
     */
    public void putProgress(IMMessage message, float progress) {
        mProgress.put(message.getUuid(), progress);
    }

    /**
     * 获取条目进度
     */
    public Float getProgress(IMMessage message) {
        return mProgress.get(message.getUuid());
    }

    @Override
    public int getItemViewType(int position) {
        IMMessage msg = getData().get(position);
        MsgTypeEnum msgType = msg.getMsgType();
        if (msgType == notification) {
            return NOTIFICATION;
        }
        if (msgType == MsgTypeEnum.text) {
            if (msg.getDirect() == MsgDirectionEnum.Out) {
                return SEND_TEXT;
            } else {
                return RECEIVE_TEXT;
            }
        }
        if (msgType == MsgTypeEnum.custom) {
            if (msg.getDirect() == MsgDirectionEnum.Out) {
                return SEND_STICKER;
            } else {
                return RECEIVE_STICKER;
            }
        }
        if (msgType == MsgTypeEnum.image) {
            if (msg.getDirect() == MsgDirectionEnum.Out) {
                return SEND_IMAGE;
            } else {
                return RECEIVE_IMAGE;
            }
        }
        if (msgType == MsgTypeEnum.video) {
            if (msg.getDirect() == MsgDirectionEnum.Out) {
                return SEND_VIDEO;
            } else {
                return RECEIVE_VIDEO;
            }
        }
        if (msgType == MsgTypeEnum.location) {
            if (msg.getDirect() == MsgDirectionEnum.Out) {
                return SEND_LOCATION;
            } else {
                return RECEIVE_LOCATION;
            }
        }
        if (msgType == MsgTypeEnum.audio) {
            if (msg.getDirect() == MsgDirectionEnum.Out) {
                return SEND_AUDIO;
            } else {
                return RECEIVE_AUDIO;
            }
        }
        if (msgType == MsgTypeEnum.file) {
            if (msg.getDirect() == MsgDirectionEnum.Out) {
                return SEND_FILE;
            } else {
                return RECEIVE_FILE;
            }
        }
        return super.getItemViewType(position);
    }

    /*================== 语音相关 begin ==================*/
    private void playAudioDelayAndSetPlayNext(IMMessage item) {
        mAudioControl.startPlayAudioDelay(CLICK_TO_PLAY_AUDIO_DELAY, item, onPlayListener);
        mAudioControl.setPlayNext(true, SessionAdapter.this, item);
    }

    private MessageAudioControl.AudioControlListener onPlayListener = new BaseAudioControl.AudioControlListener() {
        @Override
        public void onAudioControllerReady(Playable playable) {
            play();
            LogUtils.sf("语音 onAudioControllerReady");
        }

        @Override
        public void onEndPlay(Playable playable) {
            stop();
        }

        @Override
        public void updatePlayingProgress(Playable playable, long curPosition) {

        }
    };

    private void play() {
        if (mAnimationView != null && mAnimationView.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) mAnimationView.getBackground();
            animation.start();
        }
    }

    private void stop() {
        if (mAnimationView != null && mAnimationView.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animation = (AnimationDrawable) mAnimationView.getBackground();
            animation.stop();
            animation.selectDrawable(0);
        }
    }

    /*================== 语音相关 end ==================*/
    /*================== 视频相关 begin ==================*/
    public static int getImageMaxEdge() {
        return (int) (165.0 / 320.0 * ScreenUtil.screenWidth);
    }

    public static int getImageMinEdge() {
        return (int) (76.0 / 320.0 * ScreenUtil.screenWidth);
    }

    // 设置控件的长宽
    protected void setLayoutParams(int width, int height, View... views) {
        for (View view : views) {
            ViewGroup.LayoutParams maskParams = view.getLayoutParams();
            maskParams.width = width;
            maskParams.height = height;
            view.setLayoutParams(maskParams);
        }
    }

    /*================== 视频相关 end ==================*/
}
