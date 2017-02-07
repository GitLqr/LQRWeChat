package com.lqr.wechat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

/**
 * @创建者 CSDN_LQR
 * @描述 跑马灯文本控件
 * <p>
 * 支持多文本依次滚动
 * <p>
 * xml中使用方法：跟TextView一样
 * <p>
 * Activity使用方法：
 * 1、mAstNotice.init(getWindowManager(), textList);//传入要滚动的文本集合
 * 2、mAstNotice.startScroll();//执行滚动
 * 3、mAstNotice.setOnNoticeClickListenter//可设置控件的点击事件，附带集合的当前下标
 */
public class MarqueeTextView extends TextView implements OnClickListener {
    public final static String TAG = MarqueeTextView.class.getSimpleName();

    private float textLength = 0f;//文本长度
    private float viewWidth = 0f;
    private float step = 0f;//文字的横坐标
    private float y = 0f;//文字的纵坐标
    private float temp_view_plus_text_length = 0.0f;//用于计算的临时变量
    private float temp_view_plus_two_text_length = 0.0f;//用于计算的临时变量
    public boolean isStarting = false;//是否开始滚动
    private Paint paint = null;//绘图样式
    private String text = "";//文本内容

    public MarqueeTextView(Context context) {
        super(context);
        initView();
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private List<String> mTextList;
    private int mCurPosition = 0;

    private void initView() {
        setOnClickListener(this);
    }

    public void init(WindowManager windowManager, List<String> textList) {
        paint = getPaint();
        mTextList = textList;

        mCurPosition = 0;
        text = mTextList.get(mCurPosition);

        textLength = paint.measureText(text);
        viewWidth = getWidth();
        if (viewWidth == 0) {
            if (windowManager != null) {
                Display display = windowManager.getDefaultDisplay();
                viewWidth = display.getWidth();
            }
        }
        step = textLength;
        temp_view_plus_text_length = viewWidth + textLength;
        temp_view_plus_two_text_length = viewWidth + textLength * 2;
        y = getTextSize() + getPaddingTop();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.step = step;
        ss.isStarting = isStarting;

        return ss;

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        step = ss.step;
        isStarting = ss.isStarting;
    }

    public static class SavedState extends BaseSavedState {
        public boolean isStarting = false;
        public float step = 0.0f;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBooleanArray(new boolean[]{isStarting});
            out.writeFloat(step);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
        };

        private SavedState(Parcel in) {
            super(in);
            boolean[] b = null;
            in.readBooleanArray(b);
            if (b != null && b.length > 0)
                isStarting = b[0];
            step = in.readFloat();
        }
    }

    public void startScroll() {
        isStarting = true;
        invalidate();
    }


    public void stopScroll() {
        isStarting = false;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        canvas.drawText(text, temp_view_plus_text_length - step, y, paint);
        if (!isStarting) {
            return;
        }
        step += 1.5;//1.5为文字滚动速度。
        if (step > temp_view_plus_two_text_length) {
            step = textLength;
            text = getNextText();
            textLength = paint.measureText(text);
            temp_view_plus_text_length = viewWidth + textLength;
            temp_view_plus_two_text_length = viewWidth + textLength * 2;
        }
        invalidate();
    }

    private String getNextText() {
        if (++mCurPosition >= mTextList.size()) {
            mCurPosition = 0;
        }
        return mTextList.get(mCurPosition);
    }

    @Override
    public void onClick(View v) {
//        if (isStarting)
//            stopScroll();
//        else
//            startScroll();

        if (mOnNoticeClickListenter != null) {
            mOnNoticeClickListenter.onNoticeClick(mCurPosition);
        }

    }

    OnNoticeClickListenter mOnNoticeClickListenter;

    public void setOnNoticeClickListenter(OnNoticeClickListenter onNoticeClickListenter) {
        mOnNoticeClickListenter = onNoticeClickListenter;
    }

    public interface OnNoticeClickListenter {
        void onNoticeClick(int curPosition);
    }
}