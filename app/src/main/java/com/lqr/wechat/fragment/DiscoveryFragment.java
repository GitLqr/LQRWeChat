package com.lqr.wechat.fragment;

import android.content.Intent;
import android.view.View;

import com.lqr.wechat.AppConst;
import com.lqr.wechat.R;
import com.lqr.wechat.activity.NearbyPerpleActivity;
import com.lqr.wechat.activity.ScanActivity;
import com.lqr.wechat.activity.WebViewActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @创建者 CSDN_LQR
 * @描述 发现
 */
public class DiscoveryFragment extends BaseFragment {

    private Intent mIntent;

    @OnClick({R.id.oivScan, R.id.oivNearby, R.id.oivShop, R.id.oivGame})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.oivScan:
                startActivity(new Intent(getActivity(), ScanActivity.class));
                break;
            case R.id.oivNearby:
                startActivity(new Intent(getActivity(), NearbyPerpleActivity.class));
                break;
            case R.id.oivShop:
                mIntent = new Intent(getActivity(), WebViewActivity.class);
                mIntent.putExtra("url", AppConst.Url.SHOP);
                mIntent.putExtra("title", "京东购物");
                startActivity(mIntent);
                break;
            case R.id.oivGame:
                mIntent = new Intent(getActivity(), WebViewActivity.class);
                mIntent.putExtra("url", AppConst.Url.GAME);
                mIntent.putExtra("title", "微信游戏");
                startActivity(mIntent);
                break;
        }
    }

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_discovery, null);
        ButterKnife.inject(this, view);
        return view;
    }
}
