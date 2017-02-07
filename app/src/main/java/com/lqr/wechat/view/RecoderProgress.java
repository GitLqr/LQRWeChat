package com.lqr.wechat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.lqr.wechat.R;

/**
 * @创建者 CSDN_LQR
 * @描述 Android仿微信录制小视频的进度条
 */

public class RecoderProgress extends View {


    private Paint mPaint = new Paint();

    private volatile State mState = State.PAUSE;

    private int maxRecoderTime = 10000;

    private int minRecoderTime = 2000;

    private int afProgressColor = 0xFF00FF00;

    private int bfProgressColor = 0xFFFC2828;

    private long startTime;

    private Context mContext;

    public RecoderProgress(Context context) {
        super(context, null);
    }


    public RecoderProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecoderProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecoderProgress);
        afProgressColor = a.getColor(R.styleable.RecoderProgress_af_progress_color, afProgressColor);
        bfProgressColor = a.getColor(R.styleable.RecoderProgress_bf_progress_color, bfProgressColor);
        maxRecoderTime = a.getInteger(R.styleable.RecoderProgress_max_recoder_time, maxRecoderTime);
        minRecoderTime = a.getInt(R.styleable.RecoderProgress_min_recoder_time, minRecoderTime);
        a.recycle();


        init(context);
    }


    private void init(Context context) {
        this.mContext = context;
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(bfProgressColor);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long currTime = System.currentTimeMillis();

        if (mState == State.START) {
            int measuredWidth = getMeasuredWidth();

            float mSpeed = measuredWidth / 2.0f / maxRecoderTime; //速度   = 甲的距离 ／ 总时间

            float druTime = (currTime - startTime);   // 时间

            if (druTime >= minRecoderTime) {
                mPaint.setColor(afProgressColor);
            }


            float dist = mSpeed * druTime; //甲 在druTime 行走的距离

            if (dist < measuredWidth / 2.0f) {  //判断是否到达终点

                canvas.drawRect(dist, 0.0f, measuredWidth - dist, getMeasuredHeight(), mPaint);//绘制进度条
                invalidate();

            }
        } else {
            return;
        }
        canvas.drawRect(0.0f, 0.0f, 0.0f, getMeasuredHeight(), mPaint);
    }


    public void startAnimation() {
        if (mState != State.START) {
            mState = State.START;
            this.startTime = System.currentTimeMillis();
            invalidate();
            setVisibility(VISIBLE);
            mPaint.setColor(bfProgressColor);
        }
    }

    public void stopAnimation() {
        if (mState != State.PAUSE) {
            mState = State.PAUSE;
            setVisibility(INVISIBLE);
        }
    }


    enum State {
        START(1, "开始"),
        PAUSE(2, "暂停");

        State(int code, String message) {
            this.code = code;
            this.message = message;
        }

        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}