package com.lqr.wechat.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.recyclerview.LQRRecyclerView;
import com.lqr.wechat.R;
import com.lqr.wechat.imageloader.ImageLoaderManager;
import com.lqr.wechat.model.NewFriend;
import com.lqr.wechat.nimsdk.NimFriendSDK;
import com.lqr.wechat.nimsdk.NimSystemSDK;
import com.lqr.wechat.nimsdk.NimUserInfoSDK;
import com.lqr.wechat.utils.StringUtils;
import com.lqr.wechat.utils.UIUtils;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @创建者 CSDN_LQR
 * @描述 新的朋友界面
 */
public class NewFriendActivity extends BaseActivity {

    private Intent mIntent;
    private List<NewFriend> mNewFriendList = new ArrayList<>();
    private LQRAdapterForRecyclerView<NewFriend> mAdapter;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.etContent)
    EditText mEtContent;
    @InjectView(R.id.tvNewFriend)
    TextView mTvNewFriend;
    @InjectView(R.id.rvNewFriend)
    LQRRecyclerView mRvNewFriend;

    @OnClick({R.id.etContent})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.etContent:
                mIntent = new Intent(this, SearchUserActivity.class);
                mIntent.putExtra(SearchUserActivity.SEARCH_TYPE, SearchUserActivity.SEARCH_USER_LOCAL);
                startActivity(mIntent);
                break;
        }
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_new_friend);
        ButterKnife.inject(this);
        initToolbar();

        //清空所有的新好友提示
        List<SystemMessageType> types = new ArrayList<>();
        types.add(SystemMessageType.AddFriend);
        NimSystemSDK.resetSystemMessageUnreadCount(types);
    }

    @Override
    public void initData() {
        showWaitingDialog("请稍等");
        //1、获取添加好友类的系统信息
        List<SystemMessageType> types = new ArrayList<>();
        types.add(SystemMessageType.AddFriend);
        InvocationFuture<List<SystemMessage>> listInvocationFuture = NimSystemSDK.querySystemMessageByType(types, 0, 100);
        listInvocationFuture.setCallback(new RequestCallback<List<SystemMessage>>() {
            @Override
            public void onSuccess(final List<SystemMessage> smList) {
                //2、从服务器上得到所有的好友用户信息
                List<String> accounts = new ArrayList<>();
                for (SystemMessage msg : smList) {
                    accounts.add(msg.getFromAccount());
                }
                if (StringUtils.isEmpty(accounts)) {
                    mTvNewFriend.setVisibility(View.GONE);
                    loadDone();
                    return;
                } else {
                    mTvNewFriend.setVisibility(View.VISIBLE);
                    NimUserInfoSDK.getUserInfosFormServer(accounts, new RequestCallback<List<NimUserInfo>>() {
                        @Override
                        public void onSuccess(List<NimUserInfo> userInfoList) {
                            mNewFriendList.clear();
                            //3、得到所有最新数据
                            for (int i = 0; i < userInfoList.size(); i++) {
                                NimUserInfo userInfo = userInfoList.get(i);
                                mNewFriendList.add(new NewFriend(userInfo, smList.get(i).getContent()));
                            }
                            setAdapter();
                            loadDone();
                        }

                        @Override
                        public void onFailed(int code) {
                            loadDone();
                        }

                        @Override
                        public void onException(Throwable exception) {
                            loadDone();
                        }
                    });
                }

            }

            @Override
            public void onFailed(int code) {
                UIUtils.showToast("加载新好友数据失败" + code);
                loadDone();
            }

            @Override
            public void onException(Throwable exception) {
                exception.printStackTrace();
                loadDone();
            }
        });
    }

    private void loadDone() {
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {
                hideWaitingDialog();
            }
        });
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new LQRAdapterForRecyclerView<NewFriend>(this, R.layout.item_new_friends_rv, mNewFriendList) {
                @Override
                public void convert(LQRViewHolderForRecyclerView helper, final NewFriend item, final int position) {
                    if (!TextUtils.isEmpty(item.getUserInfo().getAvatar())) {
                        ImageLoaderManager.LoadNetImage(item.getUserInfo().getAvatar(), (ImageView) helper.getView(R.id.ivHeader));
                    } else {
                        ((ImageView) helper.getView(R.id.ivHeader)).setImageResource(R.mipmap.default_header);
                    }
                    helper.setText(R.id.tvName, item.getUserInfo().getName()).setText(R.id.tvMsg, TextUtils.isEmpty(item.getMsg()) ? "对方请求添加你为好友" : item.getMsg());

                    if (NimFriendSDK.isMyFriend(item.getUserInfo().getAccount())) {
                        helper.setViewVisibility(R.id.tvAdded, View.VISIBLE)
                                .setViewVisibility(R.id.btnAck, View.GONE);
                    } else {
                        helper.setViewVisibility(R.id.tvAdded, View.GONE)
                                .setViewVisibility(R.id.btnAck, View.VISIBLE);
                    }

                    helper.getView(R.id.btnAck).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NimFriendSDK.ackAddFriendRequest(item.getUserInfo().getAccount(), true);
                            UIUtils.postTaskDelay(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyItemChanged(position);
                                }
                            }, 500);

                        }
                    });
                }
            };
            mRvNewFriend.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_one_text, menu);
        menu.getItem(0).setTitle("添加朋友");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.itemOne:
                startActivity(new Intent(this, AddFriendActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("新的朋友");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }


}
