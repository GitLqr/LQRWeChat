package com.lqr.wechat.view;
//添加测试编译注释

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lqr.wechat.R;
import com.nineoldandroids.view.ViewHelper;

/**
 * @创建者 CSDN_LQR
 * @描述 使用ViewRragHelper实现侧滑效果功能（跟随滑动效果，QQ6.0）
 * <p>
 * 使用时需要包含2个子布局：第一个是RelativeLayout（左侧菜单），第二个是CustomerRelativeLayout（主界面）
 */
public class DragLayout extends FrameLayout {
    private static final boolean IS_SHOW_SHADOW = true;
    //手势处理类
    private GestureDetectorCompat gestureDetector;
    //视图拖拽移动帮助类
    private ViewDragHelper dragHelper;
    //滑动监听器
    private DragListener dragListener;
    //水平拖拽的距离
    private int range;
    //宽度
    private int width;
    //高度
    private int height;
    //main视图距离在ViewGroup距离左边的距离
    private int mainLeft;
    private Context context;
    private ImageView ivShadow;
    //左侧布局
    private RelativeLayout vgLeft;
    //右侧(主界面布局)
    private CustomRelativeLayout vgMain;
    //页面状态 默认为关闭
    private Status status = Status.CLOSE;

    /**
     * 实现子View的拖拽滑动，实现Callback当中相关的方法
     */
    private final ViewDragHelper.Callback dragHelperCallback = new ViewDragHelper.Callback() {
        /**
         * 水平方向移动
         * @param child Child view being dragged
         * @param left Attempted motion along the X axis
         * @param dx Proposed change in position for left
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (mainLeft + dx < 0) {
                return 0;
            } else if (mainLeft + dx > range) {
                return range;
            } else {
                return left;
            }
        }

        /**
         * 拦截所有的子View
         * @param child Child the user is attempting to capture
         * @param pointerId ID of the pointer attempting the capture
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        /**
         * 设置水平方向滑动的最远距离
         * @param child Child view to check  屏幕宽度
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return width;
        }

        /**
         * 当拖拽的子View，手势释放的时候回调的方法， 然后根据左滑或者右滑的距离进行判断打开或者关闭
         * @param releasedChild
         * @param xvel
         * @param yvel
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (xvel > 0) {
                open();
            } else if (xvel < 0) {
                close();
            } else if (releasedChild == vgMain && mainLeft > range * 0.3) {
                open();
            } else if (releasedChild == vgLeft && mainLeft > range * 0.7) {
                open();
            } else {
                close();
            }
        }

        /**
         * 子View被拖拽 移动的时候回调的方法
         * @param changedView View whose position changed
         * @param left New X coordinate of the left edge of the view
         * @param top New Y coordinate of the top edge of the view
         * @param dx Change in X position from the last call
         * @param dy Change in Y position from the last call
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            if (changedView == vgMain) {
                mainLeft = left;
            } else {
                mainLeft = mainLeft + left;
            }
            if (mainLeft < 0) {
                mainLeft = 0;
            } else if (mainLeft > range) {
                mainLeft = range;
            }

            if (IS_SHOW_SHADOW) {
                ivShadow.layout(mainLeft, 0, mainLeft + width, height);
            }
            if (changedView == vgLeft) {
                vgLeft.layout(0, 0, width, height);
                vgMain.layout(mainLeft, 0, mainLeft + width, height);
            }

            dispatchDragEvent(mainLeft);
        }
    };

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context = context;
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        gestureDetector = new GestureDetectorCompat(context, new YScrollDetector());
        dragHelper = ViewDragHelper.create(this, dragHelperCallback);
    }

    class YScrollDetector extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
            return Math.abs(dy) <= Math.abs(dx);
        }
    }

    /**
     * 滑动相关回调接口
     */
    public interface DragListener {
        //界面打开
        public void onOpen();

        //界面关闭
        public void onClose();

        //界面滑动过程中
        public void onDrag(float percent);
    }

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    /**
     * 布局加载完成回调
     * 做一些初始化的操作
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (IS_SHOW_SHADOW) {
            ivShadow = new ImageView(context);
            ivShadow.setImageResource(R.mipmap.shadow);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView(ivShadow, 1, lp);
        }
        //左侧界面
        vgLeft = (RelativeLayout) getChildAt(0);
        //右侧(主)界面
        vgMain = (CustomRelativeLayout) getChildAt(IS_SHOW_SHADOW ? 2 : 1);
        vgMain.setDragLayout(this);
        vgLeft.setClickable(true);
        vgMain.setClickable(true);
    }

    public ViewGroup getVgMain() {
        return vgMain;
    }

    public ViewGroup getVgLeft() {
        return vgLeft;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //得到屏幕的宽度
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams params = vgLeft.getLayoutParams();
        //设置侧边的宽度为屏幕的80%
        params.width = (int) (screenWidth * 0.8f);
        vgLeft.setLayoutParams(params);

        width = vgLeft.getMeasuredWidth();
        height = vgLeft.getMeasuredHeight();
        //可以水平拖拽滑动的距离 一共为屏幕宽度的80%
//        range = (int) (width * 0.8f);
        range = (int) (screenWidth * 0.8f);

    }

    /**
     * 调用进行left和main 视图进行位置布局
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        vgLeft.layout(0, 0, width, height);
        vgMain.layout(mainLeft, 0, mainLeft + width, height);
    }

    /**
     * 拦截触摸事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev) && gestureDetector.onTouchEvent(ev);
    }

    /**
     * 将拦截的到事件给ViewDragHelper进行处理
     *
     * @param e
     * @return
     */
//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        try {
//            dragHelper.processTouchEvent(e);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 进行处理拖拽事件
     *
     * @param mainLeft
     */
    private void dispatchDragEvent(int mainLeft) {
        float percent = mainLeft / (float) range;
        //滑动动画效果
        animateView(percent);

        if (dragListener == null) {
            return;
        }
        //进行回调滑动的百分比
        dragListener.onDrag(percent);
        Status lastStatus = status;
        if (lastStatus != getStatus() && status == Status.CLOSE) {
            dragListener.onClose();
        } else if (lastStatus != getStatus() && status == Status.OPEN) {
            dragListener.onOpen();
        }
    }

    /**
     * 根据滑动的距离的比例,进行平移动画
     *
     * @param percent
     */
    private void animateView(float percent) {
        float f1 = 1 - percent * 0.5f;

        ViewHelper.setTranslationX(vgLeft, -vgLeft.getWidth() / 2.5f + vgLeft.getWidth() / 2.5f * percent);
        if (IS_SHOW_SHADOW) {
            //阴影效果视图大小进行缩放
            ViewHelper.setScaleX(ivShadow, f1 * 1.2f * (1 - percent * 0.10f));
            ViewHelper.setScaleY(ivShadow, f1 * 1.85f * (1 - percent * 0.10f));
        }
    }

    /**
     * 有加速度,当我们停止滑动的时候，该不会立即停止动画效果
     */
    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 页面状态(滑动,打开,关闭)
     */
    public enum Status {
        DRAG, OPEN, CLOSE
    }

    /**
     * 页面状态设置
     *
     * @return
     */
    public Status getStatus() {
        if (mainLeft == 0) {
            status = Status.CLOSE;
        } else if (mainLeft == range) {
            status = Status.OPEN;
        } else {
            status = Status.DRAG;
        }
        return status;
    }

    public void open() {
        open(true);
    }

    public void open(boolean animate) {
        if (animate) {
            //继续滑动
            if (dragHelper.smoothSlideViewTo(vgMain, range, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            vgMain.layout(range, 0, range * 2, height);
            dispatchDragEvent(range);
        }
    }

    public void close() {
        close(true);
    }

    public void close(boolean animate) {
        if (animate) {
            //继续滑动
            if (dragHelper.smoothSlideViewTo(vgMain, 0, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            vgMain.layout(0, 0, width, height);
            dispatchDragEvent(0);
        }
    }
}
