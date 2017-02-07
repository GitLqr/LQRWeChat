package com.lqr.wechat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lqr.wechat.fragment.BaseFragment;

import java.util.List;

/**
 * @创建者 CSDN_LQR
 * @描述 主界面中ViewPager的适配器
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragments;

    public MainPagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
