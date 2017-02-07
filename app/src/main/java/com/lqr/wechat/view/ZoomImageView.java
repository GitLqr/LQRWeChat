package com.lqr.wechat.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * @创建者 CSDN_LQR
 * @描述 可缩放ImageView
 */
public class ZoomImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener, OnScaleGestureListener, OnTouchListener {

    private ScaleGestureDetector mScaleGestureDetector = null;

    private Matrix matrix;

    private float[] matrixValues = new float[9];// 矩阵的九个值

    private boolean isOnceLayout = true;

    // 设置缩放比
    private float initScale = 1.0f;
    private float midScale = 2.0f;
    private float maxScale = 4.0f;

    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        super.setScaleType(ScaleType.MATRIX);// 设置图片通过矩阵控制

        // 实例化伸缩手势探测器
        mScaleGestureDetector = new ScaleGestureDetector(context, this);

        matrix = new Matrix();

        this.setOnTouchListener(this);

    }

    public ZoomImageView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }

    public ZoomImageView(Context context) {

        this(context, null);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // 注册全局布局监听器
        getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // 取消全局布局监听器
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    /**
     * 获取布局的参数（这个方法会调用两次）
     */
    @Override
    public void onGlobalLayout() {

        if (isOnceLayout) {// 将图片居中显示，并且伸缩图片

            Drawable drawable = this.getDrawable();

            if (drawable == null) {
                return;
            }

            // 获取父控制的宽高
            int parentWidgetWidth = this.getWidth();
            int parentWidgetHeight = this.getHeight();

            // 获取图片的宽高
            int drawableHeight = drawable.getIntrinsicHeight();
            int drawableWidth = drawable.getIntrinsicWidth();

            // 定义缩放比
            float scale = 1.0f;

            // 当图片宽度大于父控件的宽度，当高度小于父控件高度时(缩小)
            if (drawableWidth > parentWidgetWidth && drawableHeight <= parentWidgetHeight) {

                scale = parentWidgetWidth * 1.0f / drawableWidth;

            }

            // 当图片高度高于父控件，但宽度小于父控件时（缩小）
            if (drawableHeight > parentWidgetHeight && drawableWidth <= parentWidgetWidth) {

                scale = parentWidgetHeight * 1.0f / drawableHeight;

            }

            // 当图片宽度和高度都大于父控件时（缩小）
            if (drawableHeight > parentWidgetHeight && drawableWidth > parentWidgetWidth) {

                scale = Math.min(parentWidgetHeight * 1.0f / drawableHeight, parentWidgetWidth * 1.0f / drawableWidth);

            }

            // 当图片宽度和高度都小于父控件（扩大）
            if (drawableHeight < parentWidgetHeight && drawableWidth < parentWidgetWidth) {

                scale = Math.min(parentWidgetHeight * 1.0f / drawableHeight, parentWidgetWidth * 1.0f / drawableWidth);

            }

            // 设置初始化的缩放比
            initScale = scale;

            // 将图片缩放并且将图片移动到父控件中间
            float dx = (parentWidgetWidth - drawableWidth) / 2;
            float dy = (parentWidgetHeight - drawableHeight) / 2;
            matrix.postTranslate(dx, dy);

            // 将图片缩放
            matrix.postScale(scale, scale, parentWidgetWidth / 2, parentWidgetHeight / 2);

            // 将矩阵运用到图片中
            this.setImageMatrix(matrix);

            isOnceLayout = false;

        }

    }

    /**
     * 获取图片当前的缩放比
     *
     * @return
     */
    private float getCurrentImageScale() {

        matrix.getValues(matrixValues);

        return matrixValues[Matrix.MSCALE_X];

    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        // 获取图片当前的缩放比
        float currentScalse = getCurrentImageScale();

        // 拿到图片将要的缩放比例
        float scaleFactor = detector.getScaleFactor();

        if (this.getDrawable() == null) {
            return true;
        }

        // 用户将要放大图片或者用户将要缩小图片
        if ((scaleFactor > 1.0f && currentScalse < maxScale) || (scaleFactor < 1.0f && currentScalse > initScale)) {

            // 缩小时
            if (scaleFactor * currentScalse < initScale) {
                scaleFactor = initScale / currentScalse;
            }

            // 放大时
            if (scaleFactor * currentScalse > maxScale) {
                scaleFactor = maxScale / currentScalse;
            }

            matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());

            // 检查边界和中心点
            checkBorderAndCenterWhenScale();

            setImageMatrix(matrix);

        }

        return true;

    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    /**
     * 当在缩放的时候，对图片的边界和中心进行控制
     */
    private void checkBorderAndCenterWhenScale() {

        // 获取当前缩放过程中的图片的矩形
        RectF rectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        // 获取父控件的宽高
        int parentWidth = getWidth();
        int parentHeight = getHeight();

        // 如果宽度大于屏幕宽度
        if (rectF.width() >= parentWidth) {

            if (rectF.left > 0) {// 左边出现了空白

                deltaX = -rectF.left;// 往左移动

            }

            if (rectF.right < parentWidth) {// 右边出现了空白

                deltaX = parentWidth - rectF.right;// 往右移动

            }

        }

        // 如果高度大于屏幕高度
        if (rectF.height() >= parentHeight) {

            if (rectF.top > 0) {// 上边出现了空白

                deltaY = -rectF.top;// 往下移动

            }

            if (rectF.bottom < parentHeight) {// 下面出现了空白

                deltaY = parentHeight - rectF.bottom;// 往下移动

            }

        }

        // 如果宽度小于父控件的宽度
        if (rectF.width() < parentWidth) {// 要基中显示

            deltaX = parentWidth * 0.5f - rectF.right + 0.5f * rectF.width();

        }

        // 如果高度消息小于父控件的高度
        if (rectF.height() < parentHeight) {// 需要基中显示

            deltaY = parentHeight * 0.5f - rectF.bottom + 0.5f * rectF.height();

        }

        // 将图片移动到父控件中心
        matrix.postTranslate(deltaX, deltaY);

    }

    /**
     * 获取图片通过矩阵控制缩放之后的矩形
     *
     * @return
     */
    private RectF getMatrixRectF() {

        Matrix matrix2 = matrix;

        RectF rectF = new RectF();

        Drawable drawable = this.getDrawable();

        if (drawable != null) {

            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

            matrix2.mapRect(rectF);

        }

        return rectF;

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        // 用户缩放手机探测器处理触摸事件
        mScaleGestureDetector.onTouchEvent(motionEvent);

        return true;

    }

}