package com.lqr.wechat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.lqr.wechat.R;

/**
 * CSDN_LQR
 * 仿微信小视频进度条
 */
public class LQRRecordProgress extends View {

    private Paint mPaint = new Paint();
    private boolean mIsStart = false;
    private int mRecordTime;
    private int mProgressColor;
    private long mStartTime;

    public LQRRecordProgress(Context context) {
        this(context, null);
    }

    public LQRRecordProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LQRRecordProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LQRRecordProgress, defStyleAttr, 0);
        mRecordTime = a.getInteger(R.styleable.LQRRecordProgress_rp_record_time, 10) * 1000;//默认最长录制时间是10秒
        mProgressColor = a.getColor(R.styleable.LQRRecordProgress_rp_progress_color, Color.BLACK);//默认进度条是黑色
        a.recycle();

        init();
    }

    private void init() {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mProgressColor);
        stop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long currTime = System.currentTimeMillis();

        if (mIsStart) {
            int measureWidth = getMeasuredWidth();
            float mSpeed = measureWidth / 2.0f / mRecordTime;// 速度   = 一边距离 ／ 总时间
            float durTime = (currTime - mStartTime);//时间间隔
            float dist = mSpeed * durTime;//一边在durTime行走的距离

            if (dist < measureWidth / 2.0f) {//判断是否到达终点
                canvas.drawRect(dist, 0.0f, measureWidth - dist, getMeasuredHeight(), mPaint);//绘制进度条
                invalidate();
                return;
            } else {
                stop();
            }
        }
        canvas.drawRect(0.0f, 0.0f, 0.0f, getMeasuredHeight(), mPaint);
    }

    public void start() {
        mIsStart = true;
        mStartTime = System.currentTimeMillis();
        invalidate();
        setVisibility(VISIBLE);
    }

    public void stop() {
        mIsStart = false;
        setVisibility(INVISIBLE);
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
        mPaint.setColor(mProgressColor);
    }

    public int getRecordTime() {
        return mRecordTime;
    }

    public void setRecordTime(int recordTime) {
        mRecordTime = recordTime * 1000;
    }
}