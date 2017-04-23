package com.lqr.wechat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.LinearLayout;

import com.lqr.recyclerview.LQRRecyclerView;
import com.lqr.wechat.R;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.manager.BroadcastManager;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.presenter.GroupListAtPresenter;
import com.lqr.wechat.ui.view.IGroupListAtView;

import butterknife.Bind;

/**
 * @创建者 CSDN_LQR
 * @描述 群聊列表界面
 */
public class GroupListActivity extends BaseActivity<IGroupListAtView, GroupListAtPresenter> implements IGroupListAtView {

    @Bind(R.id.llGroups)
    LinearLayout mLlGroups;
    @Bind(R.id.rvGroupList)
    LQRRecyclerView mRvGroupList;

    @Override
    public void init() {
        registerBR();
    }

    @Override
    public void initData() {
        mPresenter.loadGroups();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBR();
    }

    private void registerBR() {
        BroadcastManager.getInstance(this).register(AppConst.GROUP_LIST_UPDATE, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.loadGroups();
            }
        });
    }

    private void unRegisterBR() {
        BroadcastManager.getInstance(this).unregister(AppConst.GROUP_LIST_UPDATE);
    }

    @Override
    protected GroupListAtPresenter createPresenter() {
        return new GroupListAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_group_list;
    }

    @Override
    public LinearLayout getLlGroups() {
        return mLlGroups;
    }

    @Override
    public LQRRecyclerView getRvGroupList() {
        return mRvGroupList;
    }
}
