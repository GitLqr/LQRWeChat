package com.lqr.wechat.ui.activity;

import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.presenter.FriendCircleAtPresenter;
import com.lqr.wechat.ui.view.IFriendCircleAtView;

/**
 * @创建者 CSDN_LQR
 * @描述 朋友圈
 */
public class FriendCircleActivity extends BaseActivity<IFriendCircleAtView, FriendCircleAtPresenter> implements IFriendCircleAtView {

    @Override
    protected FriendCircleAtPresenter createPresenter() {
        return new FriendCircleAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return 0;
    }
}
