package com.lqr.wechat.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @创建者 CSDN_LQR
 * @描述 不能左右划的ViewPager
 */
public class NoScrollViewPager extends ViewPager {

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollViewPager(Context context) {
        super(context);
    }

    //表示事件是否拦截，返回false表示不拦截，可以使用嵌套在内部 的viewpager相应左右划的事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * 重写onTouchEvent事件，什么都不用做
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

}
