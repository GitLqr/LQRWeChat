package com.lqr.wechat.fragment;

import android.view.View;
import android.widget.LinearLayout;

import com.lqr.wechat.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @创建者 CSDN_LQR
 * @描述 聊天界面功能页面2
 */
public class Func2Fragment extends BaseFragment {

    @InjectView(R.id.llVoice)
    LinearLayout mLlVoice;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_func_page2, null);
        ButterKnife.inject(this, view);
        return view;
    }
}
