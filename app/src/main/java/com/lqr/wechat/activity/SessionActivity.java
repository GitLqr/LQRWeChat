package com.lqr.wechat.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lqr.emoji.EmoticonPickerView;
import com.lqr.emoji.EmotionKeyboard;
import com.lqr.emoji.IEmoticonSelectedListener;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.lqr.imagepicker.ui.ImagePreviewActivity;
import com.lqr.recyclerview.LQRRecyclerView;
import com.lqr.videorecordview.LQRVideoRecordView;
import com.lqr.wechat.R;
import com.lqr.wechat.adapter.FuncPagerAdapter;
import com.lqr.wechat.adapter.SessionAdapter;
import com.lqr.wechat.factory.ThreadPoolFactory;
import com.lqr.wechat.fragment.BaseFragment;
import com.lqr.wechat.fragment.Func1Fragment;
import com.lqr.wechat.fragment.Func2Fragment;
import com.lqr.wechat.model.Contact;
import com.lqr.wechat.nimsdk.NimHistorySDK;
import com.lqr.wechat.nimsdk.NimMessageSDK;
import com.lqr.wechat.nimsdk.NimTeamSDK;
import com.lqr.wechat.nimsdk.custom.StickerAttachment;
import com.lqr.wechat.nimsdk.helper.SendImageHelper;
import com.lqr.wechat.utils.KeyBoardUtils;
import com.lqr.wechat.utils.LogUtils;
import com.lqr.wechat.utils.UIUtils;
import com.lqr.wechat.view.DotView;
import com.lqr.wechat.view.LQRRecordProgress;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.media.record.AudioRecorder;
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback;
import com.netease.nimlib.sdk.media.record.RecordType;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.AttachmentProgress;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * @创建者 CSDN_LQR
 * @描述 聊天界面
 * <p>
 * 界面加载时将从本地获取历史消息，下拉加载 时优先从本地获取，从服务器拉取历史消息次之
 */
public class SessionActivity extends BaseActivity implements IEmoticonSelectedListener, BGARefreshLayout.BGARefreshLayoutDelegate, IAudioRecordCallback, LQRVideoRecordView.OnRecordStausChangeListener {

    public static final int IMAGE_PICKER = 100;

    public static final String SESSION_ACCOUNT = "account";
    public static final String SESSION_TYPE = "type";

    //当前会话信息
    public String mSessionId;//单聊的联系人的id，群聊是群id
    private Contact mContact;
    private Team mTeam;

    public SessionTypeEnum mSessionType = SessionTypeEnum.P2P;
    //消息列表及监听
    private Observer<IMMessage> mMsgStatusObserver;
    private Observer<List<IMMessage>> mIncomingMessageObserver;
    private Observer<AttachmentProgress> mAttachmentProgressObserver;
    private List<IMMessage> mMessages = new ArrayList<>();
    private SessionAdapter mAdapter;

    private Runnable mCvMessageScrollToBottomTask = new Runnable() {
        @Override
        public void run() {
            mCvMessage.moveToPosition(mMessages.size() - 1);
        }
    };
    //获取历史消息
    private IMMessage mAnchor;
    private QueryDirectionEnum mDirection = QueryDirectionEnum.QUERY_OLD;//查询以前的消息
    private static final int LOAD_MESSAGE_COUNT = 20;
    private boolean mFirstLoad = true;

    private boolean mRemote = false;
    //底部控件
    private FuncPagerAdapter mBottomFucAdapter;
    private List<BaseFragment> mFragments;

    private EmotionKeyboard mEmotionKeyboard;
    //录音
    private AudioRecorder mAudioRecorderHelper;
    private boolean mStartRecord;
    private boolean mCanclled;

    private boolean mTouched;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.refreshLayout)
    BGARefreshLayout mRefreshLayout;

    @InjectView(R.id.cvMessage)
    LQRRecyclerView mCvMessage;

    @InjectView(R.id.llButtomFunc)
    LinearLayout mLlButtomFunc;
    @InjectView(R.id.ivAudio)
    ImageView mIvAudio;
    @InjectView(R.id.etContent)
    EditText mEtContent;
    @InjectView(R.id.btnAudio)
    Button mBtnAudio;
    @InjectView(R.id.ivEmo)
    ImageView mIvEmo;
    @InjectView(R.id.ivAdd)
    ImageView mIvAdd;

    @InjectView(R.id.btnSend)
    Button mBtnSend;
    @InjectView(R.id.flBottom)
    FrameLayout mFlBottom;
    @InjectView(R.id.epv)
    EmoticonPickerView mEpv;
    @InjectView(R.id.vpFunc)
    ViewPager mVpFunc;

    @InjectView(R.id.dv)
    DotView mDv;
    @InjectView(R.id.flPlayAudio)
    FrameLayout mFlPlayAudio;
    @InjectView(R.id.cTimer)
    Chronometer mCTimer;

    @InjectView(R.id.tvTimerTip)
    TextView mTvTimerTip;
    @InjectView(R.id.llPlayVideo)
    LinearLayout mLlPlayVideo;
    @InjectView(R.id.vrvVideo)
    LQRVideoRecordView mVrvVideo;
    @InjectView(R.id.tvTipOne)
    TextView mTvTipOne;
    @InjectView(R.id.tvTipTwo)
    TextView mTvTipTwo;
    @InjectView(R.id.rp)
    LQRRecordProgress mRp;
    @InjectView(R.id.btnVideo)
    Button mBtnVideo;
    private Observer<TeamMember> memberRemoveObserver;
    private Observer<List<TeamMember>> memberUpdateObserver;

    @OnTouch(R.id.cvMessage)
    public boolean cvTouch() {
        if (mEtContent.hasFocus()) {
            closeKeyBoardAndLoseFocus();
            return true;
        } else if (mFlBottom.getVisibility() == View.VISIBLE) {
            mFlBottom.setVisibility(View.GONE);
            closeKeyBoardAndLoseFocus();
            return true;
        }
        return false;
    }

    @OnClick({R.id.ivAudio, R.id.btnSend})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.ivAudio:
                toggleAudioButtonVisibility();
                break;
            case R.id.btnSend:
                sendTextMsg();
                break;
        }
    }

    @Override
    public void init() {
        Intent intent = getIntent();
        SessionTypeEnum sessionType = (SessionTypeEnum) intent.getSerializableExtra(SESSION_TYPE);
        if (sessionType != null) {
            mSessionType = sessionType;
        }

        mSessionId = intent.getStringExtra(SESSION_ACCOUNT);
        if (TextUtils.isEmpty(mSessionId)) {
            interrupt();
            return;
        }

        registerAllObserver();
        requestPermission();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_session);
        ButterKnife.inject(this);
        initToolbar();
        initEmotionPickerView();
        initEmotionKeyboard();
        initRefreshLayout();
        initBottomFunc();

        //解决RecyclerView局部刷新时闪烁
//        ((DefaultItemAnimator) mCvMessage.getItemAnimator()).setSupportsChangeAnimations(false);

        closeKeyBoardAndLoseFocus();
    }

    @Override
    public void initData() {
        //获取消息列表(历史记录)
        mMessages.clear();
        setAdapter();
        loadHistoryMsgFromLocal();

        if (mSessionType == SessionTypeEnum.P2P) {
            mContact = new Contact(mSessionId);
            //设置标题处的好友名称（备注/昵称）
            getSupportActionBar().setTitle(TextUtils.isEmpty(mContact.getAlias()) ? mContact.getName() : mContact.getAlias());
        } else {
            //设置标题处的群组名称
            ThreadPoolFactory.getNormalPool().execute(new Runnable() {
                @Override
                public void run() {
                    mTeam = NimTeamSDK.queryTeamBlock(mSessionId);
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            getSupportActionBar().setTitle(TextUtils.isEmpty(mTeam.getName()) ? "群聊(" + mTeam.getMemberCount() + ")" : mTeam.getName());
                        }
                    });
                }
            });

        }
    }


    @Override
    public void initListener() {
        //监听文本输入框，有值则显示发送按钮，无值则隐藏发送按钮
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(mEtContent.getText().toString())) {
                    mIvAdd.setVisibility(View.VISIBLE);
                    mBtnSend.setVisibility(View.GONE);
                } else {
                    mIvAdd.setVisibility(View.GONE);
                    mBtnSend.setVisibility(View.VISIBLE);
                }
            }
        });

        //监听文本输入框的焦点获取，当获取焦点显示软键盘时，将消息列表滚动到最后一行
        mEtContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    cvScrollToBottom();
                }
            }
        });

        //监听ViewPager的滑动，改变底部小圆点的样式
        mVpFunc.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //改变小圆点位置
                mDv.changeCurrentPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //录音按钮的触摸事件
//        "按住 说话"
//        "松开 结束"
//        "松开手指，取消发送"
        mBtnAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouched = true;
                        initAudioRecord();
                        onStartAudioRecord();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mTouched = false;
                        cancelAudioRecord(isCancelled(v, event));
                        break;
                    case MotionEvent.ACTION_UP:
                        mTouched = false;
                        hidePlayAudio();
                        onEndAudioRecord(isCancelled(v, event));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        mTouched = false;
                        hidePlayAudio();
                        onEndAudioRecord(isCancelled(v, event));
                        break;
                }
                return false;
            }
        });

        //视频按钮的触摸事件
        mBtnVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mRp.start();
                        mRp.setProgressColor(Color.parseColor("#1AAD19"));
                        mTvTipOne.setVisibility(View.VISIBLE);
                        mTvTipTwo.setVisibility(View.GONE);
                        //开始录制
                        mVrvVideo.record(SessionActivity.this);
                        break;
                    case MotionEvent.ACTION_UP:
                        mRp.stop();
                        mTvTipOne.setVisibility(View.GONE);
                        mTvTipTwo.setVisibility(View.GONE);
                        //判断时间
                        if (mVrvVideo.getTimeCount() > 3) {
                            if (!isCancelled(v, event)) {
                                onRecrodFinish();
                            } else {
                                if (mVrvVideo.getVecordFile() != null)
                                    mVrvVideo.getVecordFile().delete();
                            }
                        } else {
                            if (!isCancelled(v, event)) {
                                Toast.makeText(getApplicationContext(), "视频时长太短", Toast.LENGTH_SHORT).show();
                            } else {
                                if (mVrvVideo.getVecordFile() != null)
                                    mVrvVideo.getVecordFile().delete();
                            }
                        }
                        resetVideoRecord();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isCancelled(v, event)) {
                            mTvTipOne.setVisibility(View.GONE);
                            mTvTipTwo.setVisibility(View.VISIBLE);
                            mRp.setProgressColor(Color.parseColor("#FF1493"));
                        } else {
                            mTvTipOne.setVisibility(View.VISIBLE);
                            mTvTipTwo.setVisibility(View.GONE);
                            mRp.setProgressColor(Color.parseColor("#1AAD19"));
                        }
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        if (mSessionType == SessionTypeEnum.Team) {
            mTeam = NimTeamSDK.queryTeamBlock(mSessionId);
            getSupportActionBar().setTitle(TextUtils.isEmpty(mTeam.getName()) ? "群聊(" + mTeam.getMemberCount() + ")" : mTeam.getName());
        }
        setAdapter();
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.itemFriendInfo:
                Intent intent;
                if (mSessionType == SessionTypeEnum.P2P) {
                    intent = new Intent(SessionActivity.this, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.USER_INFO_ACCOUNT, mSessionId);
                    startActivity(intent);
                } else {
                    intent = new Intent(SessionActivity.this, TeamCheatInfoActivity.class);
                    intent.putExtra(TeamCheatInfoActivity.GROUP_CHEAT_INFO_TEAMID, mSessionId);
                    startActivityForResult(intent, 100);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {//返回多张照片
            if (data != null) {
                //是否发送原图
                boolean isOrig = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);

                for (ImageItem imageItem : images) {
                    new SendImageHelper.SendImageTask(SessionActivity.this, isOrig, imageItem, new SendImageHelper.Callback() {
                        @Override
                        public void sendImage(File file, boolean isOrig) {
                            sendImagesMsg(file);
                        }
                    }).execute();
                }
            }
        } else if (resultCode == TeamCheatInfoActivity.RESP_QUIT_TEAM || resultCode == TeamCheatInfoActivity.RESP_CHEAT_SINGLE) {
            finish();
        } else if (resultCode == TeamCheatInfoActivity.RESP_CLEAR_CHATTING_RECORD_HISTORY) {
            mAdapter.clearData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //销毁消息状态监听和消息接收监听
        unRegisterAllObserver();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }

    private void initRefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, false);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
    }

    /**
     * 初始化底部功能区
     */
    private void initBottomFunc() {
        //底部功能区
        mFragments = new ArrayList<>();
        Func1Fragment func1Fragment1 = new Func1Fragment();
        Func2Fragment func1Fragment2 = new Func2Fragment();
        mFragments.add(func1Fragment1);
        mFragments.add(func1Fragment2);
        mBottomFucAdapter = new FuncPagerAdapter(getSupportFragmentManager(), mFragments);
        mVpFunc.setAdapter(mBottomFucAdapter);

        //初始化圆点的个数及当前被选中的位置
        mDv.initData(mFragments.size(), 0);
    }

    public void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new SessionAdapter(this, mMessages);
            mCvMessage.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void registerAllObserver() {
        observeMsgStatus();
        observeReceiveMessage();
        observerAttachmentProgressObserver();
        if (mSessionType == SessionTypeEnum.Team) {
            observeMemberRemove();
            observeMemberUpdate();
        }
    }

    private void unRegisterAllObserver() {
        NimMessageSDK.observeMsgStatus(mMsgStatusObserver, false);
        NimMessageSDK.observeReceiveMessage(mIncomingMessageObserver, false);
        NimMessageSDK.observeAttachProgress(mAttachmentProgressObserver, false);
        if (mSessionType == SessionTypeEnum.Team) {
            NimTeamSDK.observeMemberRemove(memberRemoveObserver, false);
            NimTeamSDK.observeMemberUpdate(memberUpdateObserver, false);
        }
    }

    /**
     * 注册消息状态监听
     */
    private void observeMsgStatus() {
        mMsgStatusObserver = new Observer<IMMessage>() {
            @Override
            public void onEvent(IMMessage imMessage) {
                if (NimMessageSDK.isCurrentSessionMessage(imMessage, mSessionId, mSessionType)) {
                    onMessageStatusChange(imMessage);
                }
            }
        };
        NimMessageSDK.observeMsgStatus(mMsgStatusObserver, true);
    }

    /**
     * 注册消息接收监听
     */
    private void observeReceiveMessage() {
        mIncomingMessageObserver = new Observer<List<IMMessage>>() {
            @Override
            public void onEvent(List<IMMessage> imMessages) {
                if (imMessages == null || imMessages.isEmpty()) {
                    return;
                }

                //筛选出当前会话的消息
                List<IMMessage> currentMsgList = new ArrayList<>();
                for (IMMessage msg : imMessages) {
                    if (NimMessageSDK.isCurrentSessionMessage(msg, mSessionId, mSessionType)) {
                        currentMsgList.add(msg);
                    }
                }

                //获取未插入新消息前的最后一个可见位置
                int theLastOnePosition = mAdapter.getData().size() - 1;
                mAdapter.addMoreData(currentMsgList);

                //如果当前列表处于最新消息，则自动滚动到底部
                int lastVisibleItemPosition = ((GridLayoutManager) mCvMessage.getLayoutManager()).findLastVisibleItemPosition();
                if (lastVisibleItemPosition == theLastOnePosition)
                    cvScrollToBottom();

            }
        };
        NimMessageSDK.observeReceiveMessage(mIncomingMessageObserver, true);
    }

    /**
     * 消息附件上传/下载进度观察者
     */
    private void observerAttachmentProgressObserver() {
        mAttachmentProgressObserver = new Observer<AttachmentProgress>() {
            @Override
            public void onEvent(AttachmentProgress progress) {
                onAttachmentProgressChange(progress);
            }
        };
        NimMessageSDK.observeAttachProgress(mAttachmentProgressObserver, true);
    }

    private void observeMemberUpdate() {
        memberUpdateObserver = new Observer<List<TeamMember>>() {
            @Override
            public void onEvent(List<TeamMember> teamMembers) {
                onResume();
            }
        };
        NimTeamSDK.observeMemberUpdate(memberUpdateObserver, true);
    }

    private void observeMemberRemove() {
        memberRemoveObserver = new Observer<TeamMember>() {
            @Override
            public void onEvent(TeamMember teamMember) {
                onResume();
            }
        };
        NimTeamSDK.observeMemberRemove(memberRemoveObserver, true);
    }

    private void onMessageStatusChange(IMMessage message) {
        int index = getItemIndex(message.getUuid());
        if (index >= 0 && index < mMessages.size()) {
            IMMessage msg = mMessages.get(index);
            msg.setStatus(message.getStatus());
            msg.setAttachStatus(message.getAttachStatus());
            mAdapter.notifyItemChanged(index);
        }
    }

    private void onAttachmentProgressChange(AttachmentProgress progress) {
        int index = getItemIndex(progress.getUuid());
        if (index >= 0 && index < mMessages.size()) {
            IMMessage item = mMessages.get(index);
            LogUtils.sf("Transferred = " + progress.getTransferred());
            LogUtils.sf("Total = " + progress.getTotal());
            float value = (float) progress.getTransferred() / (float) progress.getTotal();
            mAdapter.putProgress(item, value * 100);
            mAdapter.notifyItemChanged(index);
        }
    }

    private int getItemIndex(String uuid) {
        for (int i = 0; i < mMessages.size(); i++) {
            IMMessage message = mMessages.get(i);
            if (TextUtils.equals(message.getUuid(), uuid)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * 获取锚点
     */
    private IMMessage getAnchor() {
        if (mMessages.size() == 0) {
            return mAnchor == null ? MessageBuilder.createEmptyMessage(mSessionId, mSessionType, 0) : mAnchor;
        } else {
            int index = (mDirection == QueryDirectionEnum.QUERY_NEW ? mMessages.size() - 1 : 0);
            return mMessages.get(index);
        }
    }

    /**
     * 从本地获取历史消息
     */
    private void loadHistoryMsgFromLocal() {
        LogUtils.sf("从本地获取历史消息");
        mDirection = QueryDirectionEnum.QUERY_OLD;
        mRemote = false;
        NimHistorySDK.queryMessageListEx(getAnchor(), mDirection, LOAD_MESSAGE_COUNT, true).setCallback(loadFromRemoteCallback);
    }

    /**
     * 从服务器获取最新的历史消息
     */
//    private void loadNewMsgFromServer() {
//        LogUtils.sf("从服务器获取最新的历史消息");
//        mDirection = QueryDirectionEnum.QUERY_NEW;
//        mRemote = true;
//        NimHistorySDK.pullMessageHistoryEx(getAnchor(), new DateTime(2017,1,5,23,59,59).getMillis(), LOAD_MESSAGE_COUNT, mDirection, true).setCallback(loadFromRemoteCallback);
//    }

    /**
     * 从服务器获取旧的历史消息
     */
    private void loadHistoryMsgFromRemote() {
        LogUtils.sf("从服务器获取旧的历史消息");
        mDirection = QueryDirectionEnum.QUERY_OLD;
        mRemote = true;
        NimHistorySDK.pullMessageHistory(getAnchor(), LOAD_MESSAGE_COUNT, true).setCallback(loadFromRemoteCallback);
    }

    private boolean mIsFirstLoadHistory = true;

    RequestCallback<List<IMMessage>> loadFromRemoteCallback = new RequestCallbackWrapper<List<IMMessage>>() {
        @Override
        public void onResult(int code, List<IMMessage> result, Throwable exception) {
            if (code != ResponseCode.RES_SUCCESS || exception != null) {
                return;
            }

            if (result == null)
                return;

            //是第一次加载本地历史消息时，不需要从服务器加载数据
            if (mIsFirstLoadHistory) {
                mIsFirstLoadHistory = false;
            }
            //如果从本地获取消息已经没有了，则从服务器获取消息
            else if (result.size() == 0 && !mRemote) {
                loadHistoryMsgFromRemote();
                return;
            }

            onMessageLoaded(result);
        }
    };


    /**
     * 历史消息加载处理
     *
     * @param messages
     */
    private void onMessageLoaded(List<IMMessage> messages) {
        if (mRemote) {
            Collections.reverse(messages);
        }

        if (mFirstLoad && mMessages.size() > 0) {
            // 在第一次加载的过程中又收到了新消息，做一下去重
            for (IMMessage message : messages) {
                for (IMMessage item : mMessages) {
                    if (item.isTheSame(message)) {
                        mAdapter.removeItem(item);
                        break;
                    }
                }
            }
        }

        if (mFirstLoad && mAnchor != null) {
            mAdapter.addLastItem(mAnchor);
        }

        if (mDirection == QueryDirectionEnum.QUERY_NEW) {
            mAdapter.addMoreData(messages);
        } else {
            mAdapter.addNewData(messages);
        }

        if (mFirstLoad) {
            cvScrollToBottom();
        } else {
            if (messages.size() > 0) {
                mCvMessage.moveToPosition(messages.size() - 1);
            }
        }

        mRefreshLayout.endRefreshing();

        mFirstLoad = false;
    }

    /**
     * 发送文字消息
     */
    public void sendTextMsg() {
        String content = mEtContent.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            IMMessage message = NimMessageSDK.createTextMessage(mSessionId, mSessionType, content);
            sendMsg(message);
            mEtContent.setText("");
        }
    }

    /**
     * 发送贴图消息
     *
     * @param stickerAttachment
     */
    private void sendStickerMsg(StickerAttachment stickerAttachment) {
        IMMessage stickerMessage = NimMessageSDK.createCustomMessage(mSessionId, mSessionType, "贴图消息", stickerAttachment);
        sendMsg(stickerMessage);
    }

    /**
     * 发送图片消息
     */
    private void sendImagesMsg(File image) {
        IMMessage message = NimMessageSDK.createImageMessage(mSessionId, mSessionType, image.getAbsoluteFile(), image.getName());
        sendMsg(message);
    }

    /**
     * 发送语音消息
     */
    private void sendAudioMsg(File audioFile, long audioLength) {
        IMMessage msg = NimMessageSDK.createAudioMessage(mSessionId, mSessionType, audioFile, audioLength);
        sendMsg(msg);
    }

    /**
     * 发送视频消息
     */
    private void sendVidoMsg(File videoFile, String displayName) {
        IMMessage msg = NimMessageSDK.createVideoMessage(mSessionId, mSessionType, videoFile, displayName);
        sendMsg(msg);
    }


    /**
     * 发送消息的统一步骤
     */
    private void sendMsg(IMMessage message) {
        NimMessageSDK.sendMessage(message);
        mAdapter.addLastItem(message);
        mAdapter.notifyDataSetChanged();
        cvScrollToBottom();
    }

    /**
     * 获取焦点，并打开键盘
     */
    private void openKeyBoardAndGetFocus() {
        mEtContent.requestFocus();
        KeyBoardUtils.openKeybord(mEtContent, this);
    }

    /**
     * 失去焦点，并关闭键盘
     */
    private void closeKeyBoardAndLoseFocus() {
        mEtContent.clearFocus();
        KeyBoardUtils.closeKeybord(mEtContent, this);
        mFlBottom.setVisibility(View.GONE);
    }

    /**
     * 消息列表滚动至最后
     */
    private void cvScrollToBottom() {
        UIUtils.postTaskDelay(mCvMessageScrollToBottomTask, 100);
    }

    /*================== 表情、贴图相关 begin ==================*/

    /**
     * 设置表情、贴图控件
     */
    private void initEmotionPickerView() {
        mEpv.setWithSticker(true);
        mEpv.show(this);
        mEpv.attachEditText(mEtContent);
    }

    /**
     * 初始化表情软键盘
     */
    private void initEmotionKeyboard() {
        //1、创建EmotionKeyboard对象
        mEmotionKeyboard = EmotionKeyboard.with(this);
        //2、绑定输入框控件
        mEmotionKeyboard.bindToEditText(mEtContent);
        //3、绑定输入框上面的消息列表控件（这里用的是RecyclerView，其他控件也可以，注意该控件是会影响输入框位置的控件）
        mEmotionKeyboard.bindToContent(mCvMessage);
        //4、绑定输入框下面的底部区域（这里是把表情区和功能区共放在FrameLayout下，所以绑定的控件是FrameLayout）
        mEmotionKeyboard.setEmotionView(mFlBottom);
        //5、绑定表情按钮（可以绑定多个，如微信就有2个，一个是表情按钮，一个是功能按钮）
        mEmotionKeyboard.bindToEmotionButton(mIvEmo, mIvAdd);
        //6、当在第5步中绑定了多个EmotionButton时，这里的回调监听的view就有用了，注意是为了判断是否要自己来控制底部的显隐，还是交给EmotionKeyboard控制
        mEmotionKeyboard.setOnEmotionButtonOnClickListener(new EmotionKeyboard.OnEmotionButtonOnClickListener() {
            @Override
            public boolean onEmotionButtonOnClickListener(View view) {
                if (mBtnAudio.getVisibility() == View.VISIBLE) {
                    hideBtnAudio();
                }
                //输入框底部显示时
                if (mFlBottom.getVisibility() == View.VISIBLE) {
                    //表情控件显示而点击的按钮是ivAdd时，拦截事件，隐藏表情控件，显示功能区
                    if (mEpv.getVisibility() == View.VISIBLE && view.getId() == R.id.ivAdd) {
                        mEpv.setVisibility(View.GONE);
                        mLlButtomFunc.setVisibility(View.VISIBLE);
                        return true;
                        //功能区显示而点击的按钮是ivEmo时，拦截事件，隐藏功能区，显示表情控件
                    } else if (mLlButtomFunc.getVisibility() == View.VISIBLE && view.getId() == R.id.ivEmo) {
                        mEpv.setVisibility(View.VISIBLE);
                        mLlButtomFunc.setVisibility(View.GONE);
                        return true;
                    }
                } else {
                    //点击ivEmo，显示表情控件
                    if (view.getId() == R.id.ivEmo) {
                        mEpv.setVisibility(View.VISIBLE);
                        mLlButtomFunc.setVisibility(View.GONE);
                        //点击ivAdd，显示功能区
                    } else {
                        mEpv.setVisibility(View.GONE);
                        mLlButtomFunc.setVisibility(View.VISIBLE);
                    }
                }
                cvScrollToBottom();
                return false;
            }
        });
    }

    @Override
    public void onEmojiSelected(String s) {
    }

    @Override
    public void onStickerSelected(String catalog, String chartlet) {
        StickerAttachment stickerAttachment = new StickerAttachment(catalog, chartlet);
        sendStickerMsg(stickerAttachment);
    }

    /*================== 表情、贴图相关 end ==================*/
    /*================== 录制音频相关 begin ==================*/

    /**
     * 切换语音按钮显隐
     */
    public void toggleAudioButtonVisibility() {
        if (mBtnAudio.getVisibility() == View.VISIBLE) {
            hideBtnAudio();
        } else {
            showBtnAudio();
        }
        //修改图标
        mIvAudio.setImageResource(mBtnAudio.getVisibility() == View.VISIBLE ? R.mipmap.ic_cheat_keyboard : R.mipmap.ic_cheat_voice);
    }

    private void showBtnAudio() {
        mBtnAudio.setVisibility(View.VISIBLE);
        mEtContent.setVisibility(View.GONE);
        mIvEmo.setVisibility(View.GONE);
        //关闭键盘
        closeKeyBoardAndLoseFocus();
    }

    private void hideBtnAudio() {
        mBtnAudio.setVisibility(View.GONE);
        mEtContent.setVisibility(View.VISIBLE);
        mIvEmo.setVisibility(View.VISIBLE);
        //打开键盘
        openKeyBoardAndGetFocus();
    }

    private void showPlayAudio() {
        mBtnAudio.setText("松开 结束");
        mBtnAudio.setBackgroundResource(R.drawable.shape_btn_voice_press);
    }

    private void hidePlayAudio() {
        mBtnAudio.setText("按住 说话");
        mBtnAudio.setBackgroundResource(R.drawable.shape_btn_voice_normal);
        mFlPlayAudio.setVisibility(View.GONE);
    }

    /**
     * 正在进行语音录制和取消语音录制，界面展示
     */
    private void updateTimerTip(boolean cancel) {
        if (cancel) {
            mTvTimerTip.setText("松开手指，取消发送");
            mTvTimerTip.setBackgroundResource(R.drawable.shape_bottom_corner_solid_red);
            mBtnAudio.setText("松开手指，取消发送");
        } else {
            mTvTimerTip.setText("手指上滑，取消发送");
            mTvTimerTip.setBackgroundResource(0);
            mBtnAudio.setText("松开 结束");
        }
    }

    /**
     * 开始语音录制动画
     */
    private void startAudioRecordAnim() {
        mFlPlayAudio.setVisibility(View.VISIBLE);
        mCTimer.setBase(SystemClock.elapsedRealtime());//时间复位
        mCTimer.start();
    }

    /**
     * 结束语音录制动画
     */
    private void stopAudiioRecordAnim() {
        mFlPlayAudio.setVisibility(View.GONE);
        mCTimer.stop();
        mCTimer.setBase(SystemClock.elapsedRealtime());//时间复位
    }

    private static boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        if (event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth()
                || event.getRawY() < location[1] - 40) {
            return true;
        }

        return false;
    }

    /**
     * 初始化AudioRecord
     */
    private void initAudioRecord() {
        if (mAudioRecorderHelper == null)
            mAudioRecorderHelper = new AudioRecorder(this, RecordType.AAC, AudioRecorder.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND, this);
    }

    /**
     * 开始语音录制
     */
    private void onStartAudioRecord() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mStartRecord = mAudioRecorderHelper.startRecord();
        mCanclled = false;
        if (mStartRecord == false) {
            UIUtils.showToast("初始化录音失败");
            return;
        }

        if (!mTouched) {
            return;
        }

        showPlayAudio();
        updateTimerTip(false);
        startAudioRecordAnim();
    }

    /**
     * 结束语音录制
     */
    private void onEndAudioRecord(boolean cancel) {
        getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mAudioRecorderHelper.completeRecord(cancel);
        hidePlayAudio();
        stopAudiioRecordAnim();
    }

    /**
     * 取消语音录制
     */
    private void cancelAudioRecord(boolean cancel) {
        if (!mStartRecord) {
            return;
        }

        if (mCanclled == cancel) {
            return;
        }

        mCanclled = cancel;
        updateTimerTip(cancel);
    }

    @Override
    public void onRecordReady() {

    }

    @Override
    public void onRecordStart(File audioFile, RecordType recordType) {

    }

    @Override
    public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType) {
        sendAudioMsg(audioFile, audioLength);
    }

    @Override
    public void onRecordFail() {

    }

    @Override
    public void onRecordCancel() {

    }

    @Override
    public void onRecordReachedMaxTime(final int maxTime) {
        stopAudiioRecordAnim();
        showMaterialDialog("", "录音达到最大时间，是否发送？", "发送", "取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioRecorderHelper.handleEndRecord(true, maxTime);
                hideMaterialDialog();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMaterialDialog();
            }
        });
    }

    public boolean isRecording() {
        return mAudioRecorderHelper != null && mAudioRecorderHelper.isRecording();
    }

    public void requestPermission() {
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void doSomething() {
//        UIUtils.showToast("获取录音权限成功，可以正常发送语音");
    }

    @PermissionFail(requestCode = 100)
    public void doFailSomething() {
        UIUtils.showToast("获取录音权限失败，可能无法发送语音");
    }

    /*================== 录制音频相关 end ==================*/
    /*================== 录制视频相关 begin ==================*/

    @Override
    public void onBackPressed() {
        if (mIsPlayVideoShown) {
            hidePlayVideo();
            return;
        }
        super.onBackPressed();
    }

    private boolean mIsPlayVideoShown = false;//标记小视频视图是否显示

    public void showPlayVideo() {
        mLlPlayVideo.setVisibility(View.VISIBLE);
        initVideoRecord();
        mIsPlayVideoShown = true;
    }

    public void hidePlayVideo() {
        mLlPlayVideo.setVisibility(View.GONE);
        releaseVideoRecord();
        mIsPlayVideoShown = false;
        cvTouch();
    }

    public void initVideoRecord() {
        UIUtils.postTaskDelay(new Runnable() {
            @Override
            public void run() {
                mVrvVideo.openCamera();
            }
        }, 1000);
    }

    public void releaseVideoRecord() {
        mVrvVideo.stop();
    }

    /**
     * 停止录制（释放相机后重新打开相机）
     */
    public void resetVideoRecord() {
        mVrvVideo.stop();
        mVrvVideo.openCamera();
    }

    @Override
    public void onRecrodFinish() {
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {
                mTvTipOne.setVisibility(View.GONE);
                mTvTipTwo.setVisibility(View.GONE);
                resetVideoRecord();
                //发送视频
                sendVidoMsg(mVrvVideo.getVecordFile(), mVrvVideo.getVecordFile().getName());
            }
        });
    }

    @Override
    public void onRecording(int timeCount, int recordMaxTime) {

    }

    @Override
    public void onRecordStart() {

    }

    /*================== 录制视频相关 end ==================*/
    /*================== 下拉刷新、上拉加载更多监听 begin ==================*/
    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        loadHistoryMsgFromRemote();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }


    /*================== 下拉刷新、上拉加载更多监听 end ==================*/
}
