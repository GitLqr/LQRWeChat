package com.lqr.wechat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @创建者 CSDN_LQR
 * @描述 解决ScrollView下嵌套GridView显示不全的问题，此问题主要是由于GridView都是可滑动的控件，嵌套在ScrollView下时需要重写onMeasure方法。
 */
public class MyGridView extends GridView {
    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView(Context context) {
        super(context);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}