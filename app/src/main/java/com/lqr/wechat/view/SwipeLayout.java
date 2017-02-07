package com.lqr.wechat.view;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * @创建者 CSDN_LQR
 * @描述 侧滑删除布局
 */
public class SwipeLayout extends FrameLayout {

    private View contentView;
    private View deleteView;
    private int contentWidth;
    private int deleteWidth;
    private ViewDragHelper dragHelper;
    private int dragWidth;

    private int STATE_OPEN = 0;
    private int STATE_CLOSE = 1;
    private int mState = STATE_CLOSE;
    float touchSlop;

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {

        dragHelper = ViewDragHelper.create(this, callback);
        // 获取系统认为的滑动的临界值
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * 将拦截事件交给ViewDragHelper处理
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 这里只是为了保证onTouchEvent可以执行
        if (!SwipeLayoutManager.getInstance().isCouldSwipe(SwipeLayout.this)) {
            return true;
        }
        return dragHelper.shouldInterceptTouchEvent(ev);
    }


    float downX, downY;
    long downTime;

    /**
     * 将触摸事件交给ViewDragHelper处理
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // SwipeLayout不可以侧滑时，关闭已经打开的SwipeLayout
        if (!SwipeLayoutManager.getInstance().isCouldSwipe(SwipeLayout.this)) {
            SwipeLayoutManager.getInstance().closeOpenInstance();
            return false;  // 这里返回true崩溃什么鬼 。
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                // 记录按下的时间
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:

                float moveX = event.getX();
                float moveY = event.getY();

                // 水平滑动,不让listView拦截事件
                if (Math.abs(moveY - downY) < Math.abs(moveX - downX)) {
                    // 请求父View不拦截事件
                    requestDisallowInterceptTouchEvent(true);
                }

                break;
            case MotionEvent.ACTION_UP:
                // 记录抬起的时间点
                long upTime = System.currentTimeMillis();
                // 计算抬起的坐标
                float upX = event.getX();
                float upY = event.getY();
                // 计算按下和抬起的时间差
                long touchDuration = upTime - downTime;
                // 计算按下点和抬起点的距离
                float touchD = getDistanceBetween2Points(new PointF(downX, downY), new PointF(upX, upY));

                // 模拟点击事件
                if (touchDuration < 400 && touchD < touchSlop) {
                    // 打开状态则关闭，否则执行点击事件
                    if (SwipeLayoutManager.getInstance().isOpenInstance(SwipeLayout.this)) {
                        SwipeLayoutManager.getInstance().closeOpenInstance();
                    } else {
                        if (listener != null) {
                            listener.onClick();
                        }
                    }

                }
                break;
        }

        dragHelper.processTouchEvent(event);
        return true;
    }

    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        /** 确定需要触摸的View */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //Toast.makeText(getContext(), "事件捕获", Toast.LENGTH_SHORT).show();
            return child == contentView || child == deleteView;
        }

        /** View在水平方向的拖拽范围，不要返回0 */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return dragWidth;
        }

        /**
         * 控制子View在水平方向移动
         * @param child 拖拽的View
         * @param left  手指滑动之后子ViewDragHelper认为的View的left
         * @param dx    手指在水平方向移动的距离
         * @return 子View最终的left
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contentView) {
                left = left > 0 ? 0 : left;
                left = left < -dragWidth ? -dragWidth : left;
            } else if (child == deleteView) {
                left = left > contentWidth ? contentWidth : left;
                left = left < contentWidth - deleteWidth ? contentWidth - deleteWidth
                        : left;
            }
            return left;
        }

        /**
         * View位置改变时调用，一般用来做伴随移动和判断状态执行相应的操作
         * @param changedView
         * @param left View当前的left
         * @param top  View当前的top
         * @param dx   View的水平移动距离
         * @param dy   View的竖直移动距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == contentView) {

                int newLeft = deleteView.getLeft() + dx;
                deleteView.layout(newLeft, getTop(), newLeft + deleteWidth, getBottom());

            } else if (changedView == deleteView) {
                contentView.layout(left - contentWidth, contentView.getTop(),
                        left, contentView.getBottom());
            }

            // 处理打开与关闭的逻辑
            if (contentView.getLeft() == -deleteWidth && mState == STATE_CLOSE) {
                mState = STATE_OPEN;

                // 记录打开的SwipeLayout
                SwipeLayoutManager.getInstance().setOpenInstance(SwipeLayout.this);

            } else if (contentView.getLeft() == 0 && mState == STATE_OPEN) {
                mState = STATE_CLOSE;
                SwipeLayoutManager.getInstance().closeOpenInstance();
            }
        }

        /** 手指抬起的时候执行 */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            int width = contentWidth - dragWidth / 2;
            if (contentView.getRight() < width) {
                openDeleteMenu();
            } else {
                closeDeleteMenu();
            }
        }
    };

    public void closeDeleteMenu() {
        dragHelper.smoothSlideViewTo(contentView, 0, contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    public void openDeleteMenu() {
        dragHelper.smoothSlideViewTo(contentView, -dragWidth, contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
        }
    }

    /**
     * 对contentView和deleteView重新排版
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        contentView.layout(left, top, right, bottom);
        deleteView.layout(right, top, right + deleteWidth, bottom);
    }

    /**
     * 获取contentView和deleteView
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 做简单的异常处理
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("the swipelayout only have 2 children!");
        }
        contentView = getChildAt(0);
        deleteView = getChildAt(1);
    }

    /**
     * 获取contentView和deleteView的测量大小
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        contentWidth = contentView.getMeasuredWidth();
        deleteWidth = deleteView.getMeasuredWidth();

        dragWidth = deleteWidth;
    }

    /**
     * 获取删除区域
     */
    public View getDeleteView() {
        return deleteView;
    }

    /**
     * 获取内容区域
     */
    public View getContentView() {
        return contentView;
    }


    private OnSwipeLayoutClickListener listener;

    public void setOnSwipeLayoutClickListener(OnSwipeLayoutClickListener listener) {
        this.listener = listener;
    }

    /**
     * 点击事件回调接口
     */
    public interface OnSwipeLayoutClickListener {
        void onClick();
    }

    /**
     * 获得两点之间的距离
     *
     * @param p0
     * @param p1
     * @return
     */
    public static float getDistanceBetween2Points(PointF p0, PointF p1) {
        return (float) Math.sqrt(Math.pow(p0.y - p1.y, 2) + Math.pow(p0.x - p1.x, 2));
    }
}


