package com.lqr.wechat.ui.fragment;

import com.lqr.optionitemview.OptionItemView;
import com.lqr.wechat.R;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.ui.activity.MainActivity;
import com.lqr.wechat.ui.activity.ScanActivity;
import com.lqr.wechat.ui.base.BaseFragment;
import com.lqr.wechat.ui.presenter.DiscoveryFgPresenter;
import com.lqr.wechat.ui.view.IDiscoveryFgView;

import butterknife.Bind;

/**
 * @创建者 CSDN_LQR
 * @描述 发现界面
 */
public class DiscoveryFragment extends BaseFragment<IDiscoveryFgView, DiscoveryFgPresenter> implements IDiscoveryFgView {

    @Bind(R.id.oivScan)
    OptionItemView mOivScan;
    @Bind(R.id.oivShop)
    OptionItemView mOivShop;
    @Bind(R.id.oivGame)
    OptionItemView mOivGame;

    @Override
    public void initListener() {
        mOivScan.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivity(ScanActivity.class));
        mOivShop.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.JD));
        mOivGame.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.GAME));
    }

    @Override
    protected DiscoveryFgPresenter createPresenter() {
        return new DiscoveryFgPresenter((MainActivity) getActivity());
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_discovery;
    }
}
