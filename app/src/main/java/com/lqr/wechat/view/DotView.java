package com.lqr.wechat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @创建者 CSDN_LQR
 * @描述 表情下方的一排小圆点
 */
public class DotView extends View {

    private int mTotalPage;//总页数
    private int mCurrentPage;//当前页数

    private Context mContext;
    private Paint mPaint;//画笔
    private int mRadius;//半径
    private int mPadding;//圆之间的间距
    private int mScreenWidth;//屏幕宽度

    public DotView(Context context) {
        this(context, null);
    }

    public DotView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//设置抗锯齿
        mRadius = (int) (2.5 * mContext.getResources().getDisplayMetrics().density);
        mPadding = (int) (6 * mContext.getResources().getDisplayMetrics().density);
    }

    public void initData(int totalPage, int currentPage) {
        mTotalPage = totalPage;
        mCurrentPage = currentPage;
        invalidate();
    }

    public void changeCurrentPage(int currentPage) {
        mCurrentPage = currentPage;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mScreenWidth = getMeasuredWidth();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //1、得到一排小圆点的总宽度(圆的直径+圆间距)
        int dotWidth = mRadius * 2 * mTotalPage + mPadding * (mTotalPage - 1);
        //2、得到左空白处的宽度
        int left = (mScreenWidth - dotWidth) / 2;
        //3、绘制各个小圆点
        for (int i = 0; i < mTotalPage; i++) {

            if (i == mCurrentPage) {
                mPaint.setColor(Color.parseColor("#8C8C8C"));
            } else {
                mPaint.setColor(Color.parseColor("#BCBCBC"));
            }

            canvas.drawCircle(left + (mRadius * 2 + mPadding) * i + mRadius, mRadius, mRadius, mPaint);
        }
    }

}
