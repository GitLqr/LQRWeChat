package com.lqr.wechat.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * @创建者 林洽锐
 * @创建时间 2016/8/19 0019
 * @描述 popupwindow工厂
 */
public class PopupWindowUtils {

    /**
     * 得到在屏幕中间的popupwindow并显示（宽高都是包裹视图）
     *
     * @param contentView popupwindow要显示的视图
     * @param parentView  参考视图
     * @return
     */
    public static PopupWindow getPopupWindowInCenter(View contentView, View parentView) {
        //        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;

        return getPopupWindowInCenter(contentView, width, height, parentView);
    }

    /**
     * 得到在屏幕中间的popupwindow并显示
     *
     * @param contentView popupwindow要显示的视图
     * @param width       popupwindow的宽度
     * @param height      popupwindow的高度
     * @param parentView  参考视图
     * @return
     */
    public static PopupWindow getPopupWindowInCenter(View contentView, int width, int height, View parentView) {
        //Gravity.CENTER:在屏幕居中，无偏移
        return getPopupWindowAtLocation(contentView, width, height, parentView, Gravity.CENTER, 0, 0);
    }

    /**
     * 得到指定在某个视图内位置的popupwindow并显示
     *
     * @param contentView popupwindow要显示的视图
     * @param width       popupwindow的宽度
     * @param height      popupwindow的高度
     * @param parentView  参考视图
     * @param gravityType 在参考视图中的相对位置
     * @param xoff        x轴偏移量
     * @param yoff        y轴偏移量
     * @return
     */
    public static PopupWindow getPopupWindowAtLocation(View contentView, int width, int height, View parentView, int gravityType, int xoff, int yoff) {
        PopupWindow popupWindow = getPopupWindow(contentView, width, height);

        //在parentView中偏移xoff和yoff
        popupWindow.showAtLocation(parentView,
                gravityType, xoff, yoff);

        return popupWindow;
    }

    public static PopupWindow getPopupWindowAtLocation(View contentView, View parentView, int gravityType, int xoff, int yoff) {
        return getPopupWindowAtLocation(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, parentView, gravityType, xoff, yoff);
    }


    /**
     * 得到一个自动识别在目标控件上方或下方的pupupwindow并显示
     *
     * @param contentView popupwindow要显示的视图
     * @param width       popupwindow的宽度
     * @param activity    能得到getWindowManager()的上下文
     * @return
     */
    public static PopupWindow getPopupWindowAsDropDownParentAuto(View contentView, int width, int height, View anchorView, Activity activity) {

        //        View itemView = (View) contentView.getParent();// 得到contentView的父控件
        PopupWindow popupWindow = getPopupWindow(contentView, width, height);

        // 控制它放置的位置
        if (isShowBottom(activity, anchorView)) {// 显示popupwindow在itemView的下方，偏移量都为0
            popupWindow.showAsDropDown(anchorView, 0, 0);
        } else {// 显示popupwindow在itemView的上方，偏移量y都为-2*itemView.getHeight()
            popupWindow.showAsDropDown(anchorView, 0,
                    -2 * anchorView.getHeight());
        }

        return popupWindow;
    }


    /**
     * 得到在指定某个视图外的popupwindow并显示
     *
     * @param contentView popupwindow要显示的视图
     * @param width       popupwindow的宽度
     * @param height      popupwindow的高度
     * @param anchorView  参考视图
     * @param xoff        x轴偏移量
     * @param yoff        y轴偏移量
     * @return
     */
    public static PopupWindow getPopupWindowAsDropDown(View contentView, int width, int height, View anchorView, int xoff, int yoff) {
        PopupWindow popupWindow = getPopupWindow(contentView, width, height);
        popupWindow.showAsDropDown(anchorView, xoff, yoff);
        return popupWindow;
    }

    /**
     * 得到在指定某个视图外的popupwindow并显示(该方法只支持4.4以上)
     *
     * @param contentView popupwindow要显示的视图
     * @param width       popupwindow的宽度
     * @param height      popupwindow的高度
     * @param anchorView  参考视图
     * @param gravityType 在参考视图外的相对位置
     * @param xoff        x轴偏移量
     * @param yoff        y轴偏移量
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static PopupWindow getPopupWindowAsDropDown(View contentView, int width, int height, View anchorView, int gravityType, int xoff, int yoff) {
        PopupWindow popupWindow = getPopupWindow(contentView, width, height);
        popupWindow.showAsDropDown(anchorView, xoff, yoff, gravityType);
        return popupWindow;
    }


    /**
     * 判断popupWindow是否显示在条目的下方
     *
     * @param itemView
     * @return
     */
    private static boolean isShowBottom(Activity context, View itemView) {
        // 得到屏幕的高度
        // int heightPixels =
        // getResources().getDisplayMetrics().heightPixels;//方式1
        int screenHeight = context.getWindowManager().getDefaultDisplay().getHeight();// 方式2

        int[] location = new int[2];
        // location[0]-->x
        // location[1]-->y
        itemView.getLocationInWindow(location);
        // 得到itemView在屏幕中Y轴的值
        int itemViewY = location[1];

        // 得到itemView距离屏幕底部的距离
        int distance = screenHeight - itemViewY - itemView.getHeight();

        if (distance < itemView.getHeight()) {// 条目下方放不下popupWindow
            return false;
        } else {// 条目下方放得下popupWindow
            return true;
        }
    }

    /**
     * 最核心的pupupwindow创建方法
     *
     * @param contentView popupwindow要显示的视图
     * @param width       popupwindow的宽度
     * @param height      popupwindow的高度
     * @return
     */
    @NonNull
    private static PopupWindow getPopupWindow(View contentView, int width, int height) {
        PopupWindow popupWindow = new PopupWindow(contentView, width, height, true);
        popupWindow.setOutsideTouchable(false);
        openOutsideTouchable(popupWindow);
        return popupWindow;
    }

    /**
     * 点击popupwindow范围以外的地方时隐藏
     *
     * @param popupWindow
     */
    public static void openOutsideTouchable(PopupWindow popupWindow) {
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
    }

    /**
     * 使window变暗
     */
    public static void makeWindowDark(Activity activity) {
        makeWindowDark(activity, 0.7f);
    }

    public static void makeWindowDark(Activity activity, float alpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alpha;
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 使window变亮
     */
    public static void makeWindowLight(Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 1f;
        activity.getWindow().setAttributes(lp);
    }


}
