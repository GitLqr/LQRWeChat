package com.lqr.wechat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.lqr.wechat.R;

/**
 * @创建者 CSDN_LQR
 * @描述 快速导航条
 */
public class QuickIndexBar extends View {

    private Paint mPaint;
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics());
    private static final String[] LETTERS = new String[]{
            "↑", "☆", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z", "#"
    };
    private int mCellWidth;
    private float mCellHeight;
    private int mTouchIndex = -1;//用于记录当前触摸的索引值

    //暴露一个字母的监听
    public interface OnLetterUpdateListener {
        void onLetterUpdate(String letter);

        void onLetterCancel();
    }

    private OnLetterUpdateListener mListener;

    public OnLetterUpdateListener getOnLetterUpdateListener() {
        return mListener;
    }

    public void setOnLetterUpdateListener(OnLetterUpdateListener listener) {
        mListener = listener;
    }

    public QuickIndexBar(Context context) {
        this(context, null);
    }

    public QuickIndexBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickIndexBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.side_bar));
        mPaint.setTextSize(mTextSize);
        //setBackgroundColor(Color.WHITE);
        //mPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //获取单元格的宽度和高度
        mCellWidth = getMeasuredWidth();
        mCellHeight = getMeasuredHeight() * 1.0f / LETTERS.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(Color.TRANSPARENT);

        for (int i = 0; i < LETTERS.length; i++) {
            String text = LETTERS[i];
            //计算坐标
            //x坐标为单元格宽度的一半 减去 文字宽度的一半
            int x = (int) (mCellWidth / 2.0f - mPaint.measureText(text) / 2.0f);

            Rect bounds = new Rect();
            mPaint.getTextBounds(text, 0, text.length(), bounds);
            //文本的高度
            int textHeight = bounds.height();

            //y坐标为单元格高度的一半 + 文字高度的一半 + 上面有多少个单元格的高度(index * mCellHeight)
            int y = (int) (mCellHeight / 2.0f + textHeight / 2.0f + i * mCellHeight);

            //mPaint.setColor(mTouchIndex == i ? Color.GRAY : Color.WHITE);//被选择到的字母变成灰色
            //绘制文本A-Z，此处的x，y坐标是字母左上方的坐标
            canvas.drawText(text, x, y, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = -1;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                index = (int) (event.getY() / mCellHeight);//   y值/每个单元格的高度 = 当前单元格的索引
                if (index >= 0 && index < LETTERS.length) {
                    if (index != mTouchIndex) {
                        if (mListener != null) {
                            mListener.onLetterUpdate(LETTERS[index]);
                            mTouchIndex = index;
                        }
                    }
                }
                setBackgroundColor(getResources().getColor(R.color.side_bar_pressed));
                break;
            case MotionEvent.ACTION_MOVE:
                index = (int) (event.getY() / mCellHeight);
                if (index >= 0 && index < LETTERS.length) {
                    if (index != mTouchIndex) {
                        if (mListener != null) {
                            mListener.onLetterUpdate(LETTERS[index]);
                            mTouchIndex = index;
                        }
                    }
                }
                setBackgroundColor(getResources().getColor(R.color.side_bar_pressed));
                break;
            case MotionEvent.ACTION_UP:
                mTouchIndex = -1;
                if (mListener != null) {
                    mListener.onLetterCancel();
                }
                setBackgroundColor(Color.TRANSPARENT);
                break;
        }
//        invalidate();//重新调用onDraw方法实现选中的字母更改颜色
        return true;
    }
}
