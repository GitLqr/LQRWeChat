package com.lqr.wechat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.recyclerview.LQRRecyclerView;
import com.lqr.wechat.R;
import com.lqr.wechat.nimsdk.NimHistorySDK;
import com.lqr.wechat.nimsdk.NimMessageSDK;
import com.lqr.wechat.utils.Bimp;
import com.lqr.wechat.utils.LogUtils;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @创建者 CSDN_LQR
 * @描述 文件墙界面(只查询本地记录)
 */
public class FileWallActivity extends BaseActivity {

    public static final int CHECK_RESULT_CODE = 100;

    private boolean mIsEditMode = false;//标记是否是编辑模式
    private IMMessage mCurrentMsg;
    private String mAccount;
    private SessionTypeEnum mSessionType;

    private List<IMMessage> mData = new ArrayList<>();
    private LQRAdapterForRecyclerView<IMMessage> mAdapter;

    private List<IMMessage> mCheckedData = new ArrayList<>();

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.cvFile)
    LQRRecyclerView mCvFile;

    @InjectView(R.id.llBottom)
    LinearLayout mLlBottom;
    @InjectView(R.id.rlShare)
    RelativeLayout mRlShare;
    @InjectView(R.id.rlCollect)
    RelativeLayout mRlCollect;
    @InjectView(R.id.rlDel)
    RelativeLayout mRlDel;

    @InjectView(R.id.btnShare)
    Button mBtnShare;
    @InjectView(R.id.btnCollect)
    Button mBtnCollect;
    @InjectView(R.id.btnDel)
    Button mBtnDel;


    @Override
    public void init() {
        Intent intent = getIntent();
        mAccount = intent.getStringExtra("account");
        mCurrentMsg = (IMMessage) intent.getSerializableExtra("currentMsg");
        mSessionType = (SessionTypeEnum) intent.getSerializableExtra("sessionType");
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_file_wall);
        ButterKnife.inject(this);
        initToolbar();
    }

    @Override
    public void initData() {
        setAdapter();
        loadLocalImageMessage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_one_text, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.itemOne:
                if (mIsEditMode) {//取消
                    //退出编辑模式
                    quitEditMode();
                    item.setTitle("选择");
                } else {//选择
                    //进入编辑模式
                    enterEditMode();
                    item.setTitle("取消");
                }
                updateToolbarTitleAndBottom();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void enterEditMode() {
        mIsEditMode = true;
        mCheckedData.clear();
        setAdapter();
        mLlBottom.setVisibility(View.VISIBLE);
    }

    private void quitEditMode() {
        mIsEditMode = false;
        setAdapter();
        mLlBottom.setVisibility(View.GONE);
        mCvFile.setAdapter(mAdapter);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
        getSupportActionBar().setTitle("聊天文件");
    }

    private void loadLocalImageMessage() {
        IMMessage anchor = MessageBuilder.createEmptyMessage(mAccount, mSessionType, 0);
        //查询本地100条图片信息
        NimHistorySDK.queryMessageListByType(MsgTypeEnum.image, anchor, Integer.MAX_VALUE).setCallback(new RequestCallback<List<IMMessage>>() {
            @Override
            public void onSuccess(List<IMMessage> result) {
                Collections.reverse(result);
                mAdapter.addMoreData(result);

                //滚动到上个页面正在查看的文件位置
                if (mCurrentMsg != null)
                    for (int i = 0; i < result.size(); i++) {
                        if (result.get(i).getUuid().equals(mCurrentMsg.getUuid())) {
                            mCvFile.moveToPosition(i);
                            break;
                        }
                    }
            }

            @Override
            public void onFailed(int code) {
                LogUtils.e("失败，code = " + code);
            }

            @Override
            public void onException(Throwable exception) {
                exception.printStackTrace();
            }
        });
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new LQRAdapterForRecyclerView<IMMessage>(this, R.layout.item_file_wall, mData) {
                @Override
                public void convert(LQRViewHolderForRecyclerView helper, final IMMessage item, int position) {

                    setImage(helper, item, position);

                    helper.setViewVisibility(R.id.cb, mIsEditMode ? View.VISIBLE : View.GONE)
                            .setViewVisibility(R.id.vMask, mIsEditMode ? View.VISIBLE : View.GONE)
                            .getView(R.id.root).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(FileWallActivity.this, ImageWatchActivity.class);
                            intent.putExtra("account", mAccount);
                            intent.putExtra("sessionType", mSessionType);
                            intent.putExtra("message", item);
                            intent.putExtra("isEditMode", mIsEditMode);
                            startActivityForResult(intent, CHECK_RESULT_CODE);
                        }
                    });

                    ((CheckBox) helper.getView(R.id.cb)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                mCheckedData.add(item);
                            } else {
                                mCheckedData.remove(item);
                            }
                            updateToolbarTitleAndBottom();
                        }
                    });
                }
            };
            mCvFile.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void updateToolbarTitleAndBottom() {
        if (mIsEditMode) {
            mToolbar.setTitle("已经选择" + mCheckedData.size() + "个文件");
            mBtnShare.setEnabled(mCheckedData.size() > 0 ? true : false);
            mBtnCollect.setEnabled(mCheckedData.size() > 0 ? true : false);
            mBtnDel.setEnabled(mCheckedData.size() > 0 ? true : false);
        } else
            mToolbar.setTitle("聊天文件");

    }

    private void setImage(LQRViewHolderForRecyclerView helper, IMMessage item, int position) {
        final ImageView iv = helper.getView(R.id.ivShowPic);
        final FileAttachment fa = (FileAttachment) mAdapter.getItem(position).getAttachment();

        //判断本地是否有缩略图
        if (fa.getThumbPath() == null) {
            AbortableFuture abortableFuture = NimMessageSDK.downloadAttachment(item, true);
            abortableFuture.setCallback(new RequestCallback() {
                @Override
                public void onSuccess(Object param) {
                    Bitmap bitmap = Bimp.getLoacalBitmap(fa.getThumbPath());
                    if (bitmap != null) {
                        iv.setImageBitmap(bitmap);
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
            Bitmap bitmap = Bimp.getLoacalBitmap(fa.getThumbPath());
            if (bitmap != null) {
                iv.setImageBitmap(bitmap);
            }
        }
    }
}
