package com.lqr.wechat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lqr.audio.AudioRecordManager;
import com.lqr.audio.IAudioRecordListener;
import com.lqr.emoji.EmotionKeyboard;
import com.lqr.emoji.EmotionLayout;
import com.lqr.emoji.IEmotionExtClickListener;
import com.lqr.emoji.IEmotionSelectedListener;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.imagepicker.ui.ImagePreviewActivity;
import com.lqr.recyclerview.LQRRecyclerView;
import com.lqr.wechat.R;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.db.DBManager;
import com.lqr.wechat.db.model.Groups;
import com.lqr.wechat.manager.BroadcastManager;
import com.lqr.wechat.model.data.LocationData;
import com.lqr.wechat.ui.base.BaseFragmentActivity;
import com.lqr.wechat.ui.presenter.SessionAtPresenter;
import com.lqr.wechat.ui.view.ISessionAtView;
import com.lqr.wechat.util.ImageUtils;
import com.lqr.wechat.util.UIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import butterknife.Bind;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.TypingMessage.TypingStatus;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * @创建者 CSDN_LQR
 * @描述 会话界面（单聊、群聊）
 */
public class SessionActivity extends BaseFragmentActivity<ISessionAtView, SessionAtPresenter> implements ISessionAtView, IEmotionSelectedListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    public static final int REQUEST_IMAGE_PICKER = 1000;
    public final static int REQUEST_TAKE_PHOTO = 1001;
    public final static int REQUEST_MY_LOCATION = 1002;

    public final static int SESSION_TYPE_PRIVATE = 1;
    public final static int SESSION_TYPE_GROUP = 2;

    private String mSessionId = "";
    private boolean mIsFirst = false;
    private Conversation.ConversationType mConversationType = Conversation.ConversationType.PRIVATE;

    @Bind(R.id.ibToolbarMore)
    ImageButton mIbToolbarMore;

    @Bind(R.id.llRoot)
    LinearLayout mLlRoot;
    @Bind(R.id.llContent)
    LinearLayout mLlContent;
    @Bind(R.id.refreshLayout)
    BGARefreshLayout mRefreshLayout;
    @Bind(R.id.rvMsg)
    LQRRecyclerView mRvMsg;

    @Bind(R.id.ivAudio)
    ImageView mIvAudio;
    @Bind(R.id.btnAudio)
    Button mBtnAudio;
    @Bind(R.id.etContent)
    EditText mEtContent;
    @Bind(R.id.ivEmo)
    ImageView mIvEmo;
    @Bind(R.id.ivMore)
    ImageView mIvMore;
    @Bind(R.id.btnSend)
    Button mBtnSend;

    @Bind(R.id.flEmotionView)
    FrameLayout mFlEmotionView;
    @Bind(R.id.elEmotion)
    EmotionLayout mElEmotion;
    @Bind(R.id.llMore)
    LinearLayout mLlMore;

    @Bind(R.id.rlAlbum)
    RelativeLayout mRlAlbum;
    @Bind(R.id.rlTakePhoto)
    RelativeLayout mRlTakePhoto;
    @Bind(R.id.rlLocation)
    RelativeLayout mRlLocation;
    @Bind(R.id.rlRedPacket)
    RelativeLayout mRlRedPacket;

    private EmotionKeyboard mEmotionKeyboard;

    @Override
    public void init() {
        Intent intent = getIntent();
        mSessionId = intent.getStringExtra("sessionId");
        int sessionType = intent.getIntExtra("sessionType", SESSION_TYPE_PRIVATE);
        switch (sessionType) {
            case SESSION_TYPE_PRIVATE:
                mConversationType = Conversation.ConversationType.PRIVATE;
                break;
            case SESSION_TYPE_GROUP:
                mConversationType = Conversation.ConversationType.GROUP;
                break;
        }

        initAudioRecordManager();

        //设置会话已读
        RongIMClient.getInstance().clearMessagesUnreadStatus(mConversationType, mSessionId);
        registerBR();
    }

    @Override
    public void initView() {
        mIbToolbarMore.setImageResource(R.mipmap.ic_session_info);
        mIbToolbarMore.setVisibility(View.VISIBLE);
        mElEmotion.attachEditText(mEtContent);
        initEmotionKeyboard();
        initRefreshLayout();
        setTitle();
    }

    private void setTitle() {
        if (mConversationType == Conversation.ConversationType.PRIVATE) {
            UserInfo userInfo = DBManager.getInstance().getUserInfo(mSessionId);
            if (userInfo != null)
                setToolbarTitle(userInfo.getName());
        } else if (mConversationType == Conversation.ConversationType.GROUP) {
            Groups groups = DBManager.getInstance().getGroupsById(mSessionId);
            if (groups != null)
                setToolbarTitle(groups.getName());
        }
    }

    @Override
    public void initData() {
        mPresenter.loadMessage();
    }

    @Override
    public void initListener() {
        mIbToolbarMore.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, SessionInfoActivity.class);
            intent.putExtra("sessionId", mSessionId);
            intent.putExtra("sessionType", mConversationType == mConversationType.PRIVATE ? SessionActivity.SESSION_TYPE_PRIVATE : SessionActivity.SESSION_TYPE_GROUP);
            jumpToActivity(intent);
        });
        mElEmotion.setEmotionSelectedListener(this);
        mElEmotion.setEmotionAddVisiable(true);
        mElEmotion.setEmotionSettingVisiable(true);
        mElEmotion.setEmotionExtClickListener(new IEmotionExtClickListener() {
            @Override
            public void onEmotionAddClick(View view) {
                UIUtils.showToast("add");
            }

            @Override
            public void onEmotionSettingClick(View view) {
                UIUtils.showToast("setting");
            }
        });
        mLlContent.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    closeBottomAndKeyboard();
                    break;
            }
            return false;
        });
        mRvMsg.setOnTouchListener((v, event) -> {
            closeBottomAndKeyboard();
            return false;
        });
        mIvAudio.setOnClickListener(v -> {
            if (mBtnAudio.isShown()) {
                hideAudioButton();
                mEtContent.requestFocus();
                if (mEmotionKeyboard != null) {
                    mEmotionKeyboard.showSoftInput();
                }
            } else {
                mEtContent.clearFocus();
                showAudioButton();
                hideEmotionLayout();
                hideMoreLayout();
            }
            UIUtils.postTaskDelay(() -> mRvMsg.smoothMoveToPosition(mRvMsg.getAdapter().getItemCount() - 1), 50);
        });
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEtContent.getText().toString().trim().length() > 0) {
                    mBtnSend.setVisibility(View.VISIBLE);
                    mIvMore.setVisibility(View.GONE);
                    RongIMClient.getInstance().sendTypingStatus(mConversationType, mSessionId, TextMessage.class.getAnnotation(MessageTag.class).value());
                } else {
                    mBtnSend.setVisibility(View.GONE);
                    mIvMore.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEtContent.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                UIUtils.postTaskDelay(() -> mRvMsg.smoothMoveToPosition(mRvMsg.getAdapter().getItemCount() - 1), 50);
            }
        });
        mBtnSend.setOnClickListener(v -> mPresenter.sendTextMsg());
        mBtnAudio.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    AudioRecordManager.getInstance(SessionActivity.this).startRecord();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isCancelled(v, event)) {
                        AudioRecordManager.getInstance(SessionActivity.this).willCancelRecord();
                    } else {
                        AudioRecordManager.getInstance(SessionActivity.this).continueRecord();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    AudioRecordManager.getInstance(SessionActivity.this).stopRecord();
                    AudioRecordManager.getInstance(SessionActivity.this).destroyRecord();
                    break;
            }
            return false;
        });

        mRlAlbum.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent, REQUEST_IMAGE_PICKER);
        });
        mRlTakePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, TakePhotoActivity.class);
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        });
        mRlLocation.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, MyLocationActivity.class);
            startActivityForResult(intent, REQUEST_MY_LOCATION);
        });
        mRlRedPacket.setOnClickListener(v -> mPresenter.sendRedPacketMsg());
    }

    private void initAudioRecordManager() {
        AudioRecordManager.getInstance(this).setMaxVoiceDuration(AppConst.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND);
        File audioDir = new File(AppConst.AUDIO_SAVE_DIR);
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }
        AudioRecordManager.getInstance(this).setAudioSavePath(audioDir.getAbsolutePath());
        AudioRecordManager.getInstance(this).setAudioRecordListener(new IAudioRecordListener() {

            private TextView mTimerTV;
            private TextView mStateTV;
            private ImageView mStateIV;
            private PopupWindow mRecordWindow;

            @Override
            public void initTipView() {
                View view = View.inflate(SessionActivity.this, R.layout.popup_audio_wi_vo, null);
                mStateIV = (ImageView) view.findViewById(R.id.rc_audio_state_image);
                mStateTV = (TextView) view.findViewById(R.id.rc_audio_state_text);
                mTimerTV = (TextView) view.findViewById(R.id.rc_audio_timer);
                mRecordWindow = new PopupWindow(view, -1, -1);
                mRecordWindow.showAtLocation(mLlRoot, 17, 0, 0);
                mRecordWindow.setFocusable(true);
                mRecordWindow.setOutsideTouchable(false);
                mRecordWindow.setTouchable(false);
            }

            @Override
            public void setTimeoutTipView(int counter) {
                if (this.mRecordWindow != null) {
                    this.mStateIV.setVisibility(View.GONE);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_rec);
                    this.mStateTV.setBackgroundResource(R.drawable.bg_voice_popup);
                    this.mTimerTV.setText(String.format("%s", new Object[]{Integer.valueOf(counter)}));
                    this.mTimerTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void setRecordingTipView() {
                if (this.mRecordWindow != null) {
                    this.mStateIV.setVisibility(View.VISIBLE);
                    this.mStateIV.setImageResource(R.mipmap.ic_volume_1);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_rec);
                    this.mStateTV.setBackgroundResource(R.drawable.bg_voice_popup);
                    this.mTimerTV.setVisibility(View.GONE);
                }
            }

            @Override
            public void setAudioShortTipView() {
                if (this.mRecordWindow != null) {
                    mStateIV.setImageResource(R.mipmap.ic_volume_wraning);
                    mStateTV.setText(R.string.voice_short);
                }
            }

            @Override
            public void setCancelTipView() {
                if (this.mRecordWindow != null) {
                    this.mTimerTV.setVisibility(View.GONE);
                    this.mStateIV.setVisibility(View.VISIBLE);
                    this.mStateIV.setImageResource(R.mipmap.ic_volume_cancel);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_cancel);
                    this.mStateTV.setBackgroundResource(R.drawable.corner_voice_style);
                }
            }

            @Override
            public void destroyTipView() {
                if (this.mRecordWindow != null) {
                    this.mRecordWindow.dismiss();
                    this.mRecordWindow = null;
                    this.mStateIV = null;
                    this.mStateTV = null;
                    this.mTimerTV = null;
                }
            }

            @Override
            public void onStartRecord() {
                RongIMClient.getInstance().sendTypingStatus(mConversationType, mSessionId, VoiceMessage.class.getAnnotation(MessageTag.class).value());
            }

            @Override
            public void onFinish(Uri audioPath, int duration) {
                //发送文件
                File file = new File(audioPath.getPath());
                if (file.exists()) {
                    mPresenter.sendAudioFile(audioPath, duration);
                }
            }

            @Override
            public void onAudioDBChanged(int db) {
                switch (db / 5) {
                    case 0:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_1);
                        break;
                    case 1:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_2);
                        break;
                    case 2:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_3);
                        break;
                    case 3:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_4);
                        break;
                    case 4:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_5);
                        break;
                    case 5:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_6);
                        break;
                    case 6:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_7);
                        break;
                    default:
                        this.mStateIV.setImageResource(R.mipmap.ic_volume_8);
                }
            }
        });
    }

    private boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        if (event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth()
                || event.getRawY() < location[1] - 40) {
            return true;
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsFirst) {
            mEtContent.clearFocus();
        } else {
            mIsFirst = false;
        }
        mPresenter.resetDraft();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_PICKER:
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {//返回多张照片
                    if (data != null) {
                        //是否发送原图
                        boolean isOrig = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
                        ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        Log.e("CSDN_LQR", isOrig ? "发原图" : "不发原图");//若不发原图的话，需要在自己在项目中做好压缩图片算法
                        for (ImageItem imageItem : images) {
                            File imageFileThumb;
                            File imageFileSource;
                            if (isOrig) {
                                imageFileSource = new File(imageItem.path);
                                imageFileThumb = ImageUtils.genThumbImgFile(imageItem.path);
                            } else {
                                //压缩图片
                                imageFileSource = ImageUtils.genThumbImgFile(imageItem.path);
                                imageFileThumb = ImageUtils.genThumbImgFile(imageFileSource.getAbsolutePath());
                            }
                            if (imageFileSource != null && imageFileSource != null)
                                mPresenter.sendImgMsg(imageFileThumb, imageFileSource);
                        }
                    }
                }
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    String path = data.getStringExtra("path");
                    if (data.getBooleanExtra("take_photo", true)) {
                        //照片
                        mPresenter.sendImgMsg(ImageUtils.genThumbImgFile(path), new File(path));
                    } else {
                        //小视频
                        mPresenter.sendFileMsg(new File(path));
                    }
                }
                break;
            case REQUEST_MY_LOCATION:
                if (resultCode == RESULT_OK) {
                    LocationData locationData = (LocationData) data.getSerializableExtra("location");
                    mPresenter.sendLocationMessage(locationData);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.saveDraft();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBR();
    }

    private void registerBR() {
        BroadcastManager.getInstance(this).register(AppConst.UPDATE_CURRENT_SESSION, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message message = intent.getParcelableExtra("result");
                if (message != null) {
                    if (message.getTargetId().equals(mSessionId)) {
                        mPresenter.receiveNewMessage(message);
                    }
                }
            }
        });
        BroadcastManager.getInstance(this).register(AppConst.REFRESH_CURRENT_SESSION, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.loadMessage();
            }
        });
        BroadcastManager.getInstance(this).register(AppConst.UPDATE_CURRENT_SESSION_NAME, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setTitle();
            }
        });
        BroadcastManager.getInstance(this).register(AppConst.CLOSE_CURRENT_SESSION, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        });
        RongIMClient.setTypingStatusListener((type, targetId, typingStatusSet) -> {
            //当输入状态的会话类型和targetID与当前会话一致时，才需要显示
            if (type.equals(mConversationType) && targetId.equals(mSessionId)) {
                //count表示当前会话中正在输入的用户数量，目前只支持单聊，所以判断大于0就可以给予显示
                int count = typingStatusSet.size();
                if (count > 0) {
                    Iterator<TypingStatus> iterator = typingStatusSet.iterator();
                    TypingStatus status = iterator.next();
                    String objectName = status.getTypingContentType();

                    MessageTag textTag = TextMessage.class.getAnnotation(MessageTag.class);
                    MessageTag voiceTag = VoiceMessage.class.getAnnotation(MessageTag.class);
                    //匹配对方正在输入的是文本消息还是语音消息
                    if (objectName.equals(textTag.value())) {
                        //显示“对方正在输入”
//                        setToolbarSubTitle(UIUtils.getString(R.string.SET_TEXT_TYPING_TITLE));
                        setToolbarTitle(UIUtils.getString(R.string.SET_TEXT_TYPING_TITLE));
                    } else if (objectName.equals(voiceTag.value())) {
                        //显示“对方正在讲话”
//                        setToolbarSubTitle(UIUtils.getString(R.string.SET_VOICE_TYPING_TITLE));
                        setToolbarTitle(UIUtils.getString(R.string.SET_VOICE_TYPING_TITLE));
                    }
                } else {
                    //当前会话没有用户正在输入，标题栏仍显示原来标题
//                    setToolbarSubTitle("");
                    setTitle();
                }
            }
        });
        //监听撤回
        RongIMClient.getInstance().setRecallMessageListener((messageId, recallNotificationMessage) -> mPresenter.recallMessageFromListener(messageId, recallNotificationMessage));
    }

    private void unRegisterBR() {
        BroadcastManager.getInstance(this).unregister(AppConst.UPDATE_CURRENT_SESSION);
        BroadcastManager.getInstance(this).unregister(AppConst.REFRESH_CURRENT_SESSION);
        BroadcastManager.getInstance(this).unregister(AppConst.UPDATE_CURRENT_SESSION_NAME);
        BroadcastManager.getInstance(this).unregister(AppConst.CLOSE_CURRENT_SESSION);
    }

    private void initRefreshLayout() {
        mRefreshLayout.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, false);
        refreshViewHolder.setRefreshingText("");
        refreshViewHolder.setPullDownRefreshText("");
        refreshViewHolder.setReleaseRefreshText("");
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    private void initEmotionKeyboard() {
        mEmotionKeyboard = EmotionKeyboard.with(this);
        mEmotionKeyboard.bindToEditText(mEtContent);
        mEmotionKeyboard.bindToContent(mLlContent);
        mEmotionKeyboard.setEmotionLayout(mFlEmotionView);
        mEmotionKeyboard.bindToEmotionButton(mIvEmo, mIvMore);
        mEmotionKeyboard.setOnEmotionButtonOnClickListener(view -> {
            switch (view.getId()) {
                case R.id.ivEmo:
                    UIUtils.postTaskDelay(() -> mRvMsg.smoothMoveToPosition(mRvMsg.getAdapter().getItemCount() - 1), 50);
                    mEtContent.clearFocus();
                    if (!mElEmotion.isShown()) {
                        if (mLlMore.isShown()) {
                            showEmotionLayout();
                            hideMoreLayout();
                            hideAudioButton();
                            return true;
                        }
                    } else if (mElEmotion.isShown() && !mLlMore.isShown()) {
                        mIvEmo.setImageResource(R.mipmap.ic_cheat_emo);
                        return false;
                    }
                    showEmotionLayout();
                    hideMoreLayout();
                    hideAudioButton();
                    break;
                case R.id.ivMore:
                    UIUtils.postTaskDelay(() -> mRvMsg.smoothMoveToPosition(mRvMsg.getAdapter().getItemCount() - 1), 50);
                    mEtContent.clearFocus();
                    if (!mLlMore.isShown()) {
                        if (mElEmotion.isShown()) {
                            showMoreLayout();
                            hideEmotionLayout();
                            hideAudioButton();
                            return true;
                        }
                    }
                    showMoreLayout();
                    hideEmotionLayout();
                    hideAudioButton();
                    break;
            }
            return false;
        });
    }

    private void showAudioButton() {
        mBtnAudio.setVisibility(View.VISIBLE);
        mEtContent.setVisibility(View.GONE);
        mIvAudio.setImageResource(R.mipmap.ic_cheat_keyboard);

        if (mFlEmotionView.isShown()) {
            if (mEmotionKeyboard != null) {
                mEmotionKeyboard.interceptBackPress();
            }
        } else {
            if (mEmotionKeyboard != null) {
                mEmotionKeyboard.hideSoftInput();
            }
        }
    }

    private void hideAudioButton() {
        mBtnAudio.setVisibility(View.GONE);
        mEtContent.setVisibility(View.VISIBLE);
        mIvAudio.setImageResource(R.mipmap.ic_cheat_voice);
    }

    private void showEmotionLayout() {
        mElEmotion.setVisibility(View.VISIBLE);
        mIvEmo.setImageResource(R.mipmap.ic_cheat_keyboard);
    }

    private void hideEmotionLayout() {
        mElEmotion.setVisibility(View.GONE);
        mIvEmo.setImageResource(R.mipmap.ic_cheat_emo);
    }

    private void showMoreLayout() {
        mLlMore.setVisibility(View.VISIBLE);
    }

    private void hideMoreLayout() {
        mLlMore.setVisibility(View.GONE);
    }

    private void closeBottomAndKeyboard() {
        mElEmotion.setVisibility(View.GONE);
        mLlMore.setVisibility(View.GONE);
        if (mEmotionKeyboard != null) {
            mEmotionKeyboard.interceptBackPress();
            mIvEmo.setImageResource(R.mipmap.ic_cheat_emo);
        }
    }

    @Override
    public void onBackPressed() {
        if (mElEmotion.isShown() || mLlMore.isShown()) {
            mEmotionKeyboard.interceptBackPress();
            mIvEmo.setImageResource(R.mipmap.ic_cheat_emo);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected SessionAtPresenter createPresenter() {
        return new SessionAtPresenter(this, mSessionId, mConversationType);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_session;
    }

    @Override
    public void onEmojiSelected(String key) {
//        LogUtils.e("onEmojiSelected : " + key);
    }

    @Override
    public void onStickerSelected(String categoryName, String stickerName, String stickerBitmapPath) {
//        LogUtils.e("onStickerSelected : categoryName = " + categoryName + " , stickerName = " + stickerName);
//        LogUtils.e("onStickerSelected : stickerBitmapPath = " + stickerBitmapPath);
        mPresenter.sendFileMsg(new File(stickerBitmapPath));
    }

    @Override
    public BGARefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    @Override
    public LQRRecyclerView getRvMsg() {
        return mRvMsg;
    }

    @Override
    public EditText getEtContent() {
        return mEtContent;
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mPresenter.loadMore();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}
