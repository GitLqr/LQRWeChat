package com.lqr.wechat.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lqr.optionitemview.OptionItemView;
import com.lqr.wechat.AppConst;
import com.lqr.wechat.R;
import com.lqr.wechat.imageloader.ImageLoaderManager;
import com.lqr.wechat.model.Contact;
import com.lqr.wechat.model.UserCache;
import com.lqr.wechat.nimsdk.NimBlackListSDK;
import com.lqr.wechat.nimsdk.NimFriendSDK;
import com.lqr.wechat.nimsdk.NimUserInfoSDK;
import com.lqr.wechat.utils.StringUtils;
import com.lqr.wechat.utils.UIUtils;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @创建者 CSDN_LQR
 * @描述 详细资料界面
 */
public class UserInfoActivity extends BaseActivity {

    public static final String USER_INFO_ACCOUNT = "account";

    private Intent mIntent;
    private Animation mPushBottomInAnimation;
    private Animation mPushBottomOutAnimation;
    private String mAccount;
    private Contact mContact;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    //内容区
    @InjectView(R.id.ivHeader)
    ImageView mIvHeader;
    @InjectView(R.id.tvAlias)
    TextView mTvAlias;
    @InjectView(R.id.tvAccount)
    TextView mTvAccount;
    @InjectView(R.id.tvName)
    TextView mTvName;
    @InjectView(R.id.ivGender)
    ImageView mIvGender;

    @InjectView(R.id.oivAliasAndTag)
    OptionItemView mOivAliasAndTag;
    @InjectView(R.id.llArea)
    LinearLayout mLlArea;
    @InjectView(R.id.tvArea)
    TextView mTvArea;
    @InjectView(R.id.llSignature)
    LinearLayout mLlSignature;
    @InjectView(R.id.tvSignature)
    TextView mTvSignature;

    @InjectView(R.id.btnCheat)
    Button mBtnCheat;
    @InjectView(R.id.btnVideoCheat)
    Button mBtnVideoCheat;
    @InjectView(R.id.btnAddFriend)
    Button mBtnAddFriend;

    //菜单区
    @InjectView(R.id.rlMenu)
    RelativeLayout mRlMenu;
    @InjectView(R.id.vMask)
    View mVMask;
    @InjectView(R.id.svMenu)
    ScrollView mSvMenu;

    @OnClick({R.id.oivAliasAndTag, R.id.btnCheat, R.id.btnVideoCheat, R.id.btnAddFriend, R.id.oivAlias, R.id.oivFriendsCirclePrivacySet, R.id.oivAddToBlackList, R.id.oivDelete})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.oivAliasAndTag:
                jumpToAliasActivity();
                break;
            case R.id.btnCheat:
                setResult(RESULT_OK);
                mIntent = new Intent(this, SessionActivity.class);
                mIntent.putExtra(SessionActivity.SESSION_ACCOUNT, mAccount);
                startActivity(mIntent);
                finish();
                break;
            case R.id.btnVideoCheat:
                break;
            case R.id.btnAddFriend:
                mIntent = new Intent(this, PostscriptActivity.class);
                mIntent.putExtra("account", mAccount);
                startActivity(mIntent);
                break;
            case R.id.oivAlias://修改备注
                jumpToAliasActivity();
                hideMenu();
                break;
            case R.id.oivFriendsCirclePrivacySet://修改朋友圈权限
                startActivity(new Intent(UserInfoActivity.this, FriendCirclePrivacySetActivity.class));
                hideMenu();
                break;
            case R.id.oivAddToBlackList://加入黑名单
                hideMenu();
                showMaterialDialog("加入黑名单", "加入黑名单，你将不再收到对方的消息，并且你们互相看不到对方朋友圈的更新", "确定", "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NimBlackListSDK.addToBlackList(mAccount, new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void param) {
                                UIUtils.showToast("加入黑名单成功");
                                Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailed(int code) {
                                UIUtils.showToast("加入黑名单失败" + code);
                            }

                            @Override
                            public void onException(Throwable exception) {
                                exception.printStackTrace();
                            }
                        });
                        hideMaterialDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideMaterialDialog();
                    }
                });
                break;
            case R.id.oivDelete://删除好友
                hideMenu();
                showMaterialDialog("删除联系人", "将联系人" + mContact.getDisplayName() + "删除，将同时删除与联系人的聊天记录", "删除", "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除
                        NimFriendSDK.deleteFriend(mAccount, new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void param) {
                                UIUtils.showToast("删除好友成功");
                                Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailed(int code) {
                                UIUtils.showToast("删除好友失败" + code);
                            }

                            @Override
                            public void onException(Throwable exception) {
                                exception.printStackTrace();
                            }
                        });
                        hideMaterialDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消
                        hideMaterialDialog();
                    }
                });
                break;
        }
    }

    private void jumpToAliasActivity() {
        mIntent = new Intent(UserInfoActivity.this, AliasActivity.class);
        mIntent.putExtra("contact", mContact);
        startActivityForResult(mIntent, AliasActivity.REQ_CHANGE_ALIAS);
    }

    @OnClick(R.id.vMask)
    public void mask() {
        toggleMenu();
    }

    @Override
    public void init() {
        mAccount = getIntent().getStringExtra("account");
        if (TextUtils.isEmpty(mAccount)) {
            interrupt();
            return;
        }
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_user_info);
        ButterKnife.inject(this);
        initToolbar();
        initAnimation();

        if (UserCache.getAccount().equals(mAccount)) {//自己
            mOivAliasAndTag.setVisibility(View.GONE);
            mLlArea.setVisibility(View.GONE);
            mLlSignature.setVisibility(View.GONE);
            mBtnCheat.setVisibility(View.VISIBLE);
        } else {
            if (NimFriendSDK.isMyFriend(mAccount)) {
                mBtnCheat.setVisibility(View.VISIBLE);
                mOivAliasAndTag.setVisibility(View.VISIBLE);
//                mBtnVideoCheat.setVisibility(View.VISIBLE);
                mBtnAddFriend.setVisibility(View.GONE);
            } else {
                mBtnCheat.setVisibility(View.GONE);
                mOivAliasAndTag.setVisibility(View.GONE);
//                mBtnVideoCheat.setVisibility(View.GONE);
                mBtnAddFriend.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void initData() {
        //根据账号得到好友信息
        if (NimFriendSDK.isMyFriend(mAccount)) {
            mContact = new Contact(mAccount);
            //获取好友信息并显示
            setUserInfo();
            //先信息旧信息，同时更新新的好友信息
            getUserInfoFromServer();
        } else {
            getUserInfoFromServer();
        }
    }

    private void getUserInfoFromServer() {
        NimUserInfoSDK.getUserInfoFromServer(mAccount, new RequestCallback<List<NimUserInfo>>() {
            @Override
            public void onSuccess(List<NimUserInfo> param) {
                if (param != null && param.size() > 0) {
                    mContact = new Contact(NimFriendSDK.getFriendByAccount(mAccount), param.get(0));
                    //获取好友信息并显示
                    setUserInfo();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (NimFriendSDK.isMyFriend(mAccount)) {
            new MenuInflater(this).inflate(R.menu.menu_more, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.itemMore:
                toggleMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AliasActivity.REQ_CHANGE_ALIAS && resultCode == RESULT_OK) {
            //修改备注成功
            initData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mRlMenu.getVisibility() == View.VISIBLE) {
            //隐藏
            mSvMenu.startAnimation(mPushBottomOutAnimation);
            return;
        }
        super.onBackPressed();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("详细资料");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }

    private void initAnimation() {
        mPushBottomInAnimation = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
        mPushBottomOutAnimation = AnimationUtils.loadAnimation(this, R.anim.push_bottom_out);
        mPushBottomInAnimation.setDuration(300);
        mPushBottomOutAnimation.setDuration(300);
        mPushBottomOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRlMenu.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void toggleMenu() {
        if (mRlMenu.getVisibility() == View.VISIBLE) {
            //隐藏
            mSvMenu.startAnimation(mPushBottomOutAnimation);
        } else {
            //显示
            mRlMenu.setVisibility(View.VISIBLE);
            mSvMenu.startAnimation(mPushBottomInAnimation);
        }
    }

    private void hideMenu() {
        mSvMenu.startAnimation(mPushBottomOutAnimation);
    }

    private void setUserInfo() {
        //设置头像
        if (TextUtils.isEmpty(mContact.getAvatar())) {
            mIvHeader.setImageResource(R.mipmap.default_header);
        } else {
            ImageLoaderManager.LoadNetImage(mContact.getAvatar(), mIvHeader);
        }

        //设置性别
        NimUserInfo userInfo = mContact.getUserInfo();
        if (userInfo.getGenderEnum() == GenderEnum.FEMALE) {
            mIvGender.setImageResource(R.mipmap.ic_gender_female);
        } else if (userInfo.getGenderEnum() == GenderEnum.MALE) {
            mIvGender.setImageResource(R.mipmap.ic_gender_male);
        } else {
            mIvGender.setVisibility(View.GONE);
        }

        //判断是否有起备注
        if (TextUtils.isEmpty(mContact.getAlias())) {
//            mTvAlias.setVisibility(View.GONE);
            mTvName.setVisibility(View.GONE);
        } else {
//            mTvAlias.setVisibility(View.VISIBLE);
            mTvName.setVisibility(View.VISIBLE);
        }
        mTvAlias.setText(mContact.getDisplayName());
        mTvAccount.setText("微信号:" + mContact.getAccount());
        mTvName.setText("昵称:" + mContact.getName());
        Map<String, Object> extensionMap = mContact.getUserInfo().getExtensionMap();
        if (extensionMap != null)
            mTvArea.setText(StringUtils.isEmpty(extensionMap.get(AppConst.UserInfoExt.AREA)) ? "" : (String) extensionMap.get(AppConst.UserInfoExt.AREA));
        mTvSignature.setText(mContact.getUserInfo().getSignature());
    }
}
