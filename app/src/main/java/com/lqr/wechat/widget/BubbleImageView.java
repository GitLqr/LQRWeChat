package com.lqr.wechat.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.lqr.wechat.R;

/**
 * @创建者 CSDN_LQR
 * @描述 气泡型ImageView
 */
public class BubbleImageView extends ImageView {

    private static final int LOCATION_LEFT = 0;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 1;

    private int mAngle = dp2px(10);
    private int mArrowTop = dp2px(40);
    private int mArrowWidth = dp2px(20);
    private int mArrowHeight = dp2px(20);
    private int mArrowOffset = 0;
    private int mArrowLocation = LOCATION_LEFT;

    private Rect mDrawableRect;
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private Paint mBitmapPaint;
    private Matrix mShaderMatrix;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private Paint mPaint;
    private int percent = 0;
    private boolean mShowText = true;//是否显示文字
    private boolean mShowShadow = true;//是否显示阴影

    public BubbleImageView(Context context) {
        this(context, null);
    }

    public BubbleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(attrs);
        mPaint = new Paint();
    }

    /**
     * 是否显示阴影
     */
    public void showShadow(boolean showShadow) {
        this.mShowShadow = showShadow;
        postInvalidate();
    }

    /**
     * 设置进度的百分比
     */
    public void setPercent(int percent) {
        this.percent = percent;
        postInvalidate();
    }


    /**
     * 设置进度文字是否显示
     */
    public void setProgressVisible(boolean show) {
        this.mShowText = show;
        postInvalidate();
    }

    private void initView(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.BubbleImageView);
            mAngle = (int) a.getDimension(
                    R.styleable.BubbleImageView_bubble_angle, mAngle);
            mArrowHeight = (int) a.getDimension(
                    R.styleable.BubbleImageView_bubble_arrowHeight,
                    mArrowHeight);
            mArrowOffset = (int) a.getDimension(
                    R.styleable.BubbleImageView_bubble_arrowOffset,
                    mArrowOffset);
            mArrowTop = (int) a.getDimension(
                    R.styleable.BubbleImageView_bubble_arrowTop, mArrowTop);
            mArrowWidth = (int) a.getDimension(
                    R.styleable.BubbleImageView_bubble_arrowWidth, mAngle);
            mArrowLocation = a.getInt(
                    R.styleable.BubbleImageView_bubble_arrowLocation,
                    mArrowLocation);
            mShowText = a.getBoolean(R.styleable.BubbleImageView_bubble_showText, mShowText);
            mShowShadow = a.getBoolean(R.styleable.BubbleImageView_bubble_showShadow, mShowShadow);
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        RectF rect = new RectF(getPaddingLeft(), getPaddingTop(), getRight()
                - getLeft() - getPaddingRight(), getBottom() - getTop()
                - getPaddingBottom());

        Path path = new Path();

        if (mArrowLocation == LOCATION_LEFT) {
            leftPath(rect, path);
        } else {
            rightPath(rect, path);
        }

        canvas.drawPath(path, mBitmapPaint);
        drawText(canvas, mAngle);
    }

    /**
     * 画进度文字和设置透明度
     *
     * @param canvas
     * @param radiusPx 圆角的半径
     */
    private void drawText(Canvas canvas, int radiusPx) {

        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setStyle(Paint.Style.FILL);

        if (mShowShadow) {//根据是否要画阴影
            // 画阴影部分
            mPaint.setColor(Color.parseColor("#70000000"));// 半透明
            Rect shadowRect = null;
            if (mArrowLocation == LOCATION_LEFT) {
                //如果是在左边
                shadowRect = new Rect(mArrowWidth, 0, getWidth(), getHeight() - getHeight()
                        * percent / 100);//阴影的宽度（图片的宽度）为ImageView的宽度减去箭头的宽度
            } else {
                shadowRect = new Rect(0, 0, getWidth() - mArrowWidth, getHeight() - getHeight()
                        * percent / 100);//阴影的宽度（图片的宽度）为ImageView的宽度减去箭头的宽度
            }
            RectF shadowRectF = new RectF(shadowRect);
            //shadowRectF.set(0, 0, getWidth(), getHeight() - getHeight()* percent / 100 );
            canvas.drawRoundRect(shadowRectF, radiusPx, radiusPx, mPaint);
        }


        if (mShowText) {//是否画文字
            //画文字
            mPaint.setTextSize(30);
            mPaint.setColor(Color.parseColor("#FFFFFF"));
            mPaint.setStrokeWidth(2);

            Rect rect = null;
            int marginLeft = 0;//文字的左边距
            if (mArrowLocation == LOCATION_LEFT) {//如果是向左的
                rect = new Rect(mArrowWidth, 0, 0, 0);
                marginLeft = (getWidth() - mArrowWidth) / 2;
            } else {
                rect = new Rect(mArrowWidth, 0, 0, 0);
                marginLeft = getWidth() / 2 - mArrowWidth;
            }
            mPaint.getTextBounds("100%", 0, "100%".length(), rect);// 确定文字的宽度
            canvas.drawText(percent + "%", marginLeft,
                    getHeight() / 2, mPaint);
        }
    }

    public void rightPath(RectF rect, Path path) {
        path.moveTo(mAngle, rect.top);
        path.lineTo(rect.width(), rect.top);
        path.arcTo(new RectF(rect.right - mAngle * 2 - mArrowWidth, rect.top,
                rect.right - mArrowWidth, mAngle * 2 + rect.top), 270, 90);
        path.lineTo(rect.right - mArrowWidth, mArrowTop);
        path.lineTo(rect.right, mArrowTop - mArrowOffset);
        path.lineTo(rect.right - mArrowWidth, mArrowTop + mArrowHeight);
        path.lineTo(rect.right - mArrowWidth, rect.height() - mAngle);
        path.arcTo(new RectF(rect.right - mAngle * 2 - mArrowWidth, rect.bottom
                - mAngle * 2, rect.right - mArrowWidth, rect.bottom), 0, 90);
        path.lineTo(rect.left, rect.bottom);
        path.arcTo(new RectF(rect.left, rect.bottom - mAngle * 2, mAngle * 2
                + rect.left, rect.bottom), 90, 90);
        path.lineTo(rect.left, rect.top);
        path.arcTo(new RectF(rect.left, rect.top, mAngle * 2 + rect.left,
                mAngle * 2 + rect.top), 180, 90);
        path.close();
    }

    public void leftPath(RectF rect, Path path) {
        path.moveTo(mAngle + mArrowWidth, rect.top);
        path.lineTo(rect.width(), rect.top);
        path.arcTo(new RectF(rect.right - mAngle * 2, rect.top, rect.right,
                mAngle * 2 + rect.top), 270, 90);
        path.lineTo(rect.right, rect.top);
        path.arcTo(new RectF(rect.right - mAngle * 2, rect.bottom - mAngle * 2,
                rect.right, rect.bottom), 0, 90);
        path.lineTo(rect.left + mArrowWidth, rect.bottom);
        path.arcTo(new RectF(rect.left + mArrowWidth, rect.bottom - mAngle * 2,
                mAngle * 2 + rect.left + mArrowWidth, rect.bottom), 90, 90);
        path.lineTo(rect.left + mArrowWidth, mArrowTop + mArrowHeight);
        path.lineTo(rect.left, mArrowTop - mArrowOffset);
        path.lineTo(rect.left + mArrowWidth, mArrowTop);
        path.lineTo(rect.left + mArrowWidth, rect.top);
        path.arcTo(new RectF(rect.left + mArrowWidth, rect.top, mAngle * 2
                + rect.left + mArrowWidth, mAngle * 2 + rect.top), 180, 90);

        path.close();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
                        COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setup() {
        if (mBitmap == null) {
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        updateShaderMatrix();
        invalidate();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix = new Matrix();
        mShaderMatrix.set(null);

        mDrawableRect = new Rect(0, 0, getRight() - getLeft(), getBottom()
                - getTop());

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width()
                * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }
}
