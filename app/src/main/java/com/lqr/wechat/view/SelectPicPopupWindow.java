package com.lqr.wechat.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.lqr.wechat.R;


/**
 * @创建者 CSDN_LQR
 * @描述 选择拍照或相片的popupwindow
 */
public class SelectPicPopupWindow extends PopupWindow {
    public SelectPicPopupWindow(Context context, OnClickListener listener) {
        View contentView = View.inflate(context, R.layout.layout_pupup, null);
        this.setContentView(contentView);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 点击popupwindows范围以外的地方,让其消失
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setOutsideTouchable(true);
        // 添加动画
        this.setAnimationStyle(R.style.PopupAnimation);
        /*	// 控制它放置的位置
            this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);*/

        Button btnTakePhoto = (Button) contentView.findViewById(R.id.btnTakePhoto);
        Button btnPickPhoto = (Button) contentView.findViewById(R.id.btnPickPhoto);
        Button btnCancel = (Button) contentView.findViewById(R.id.btnCancel);

        btnTakePhoto.setOnClickListener(listener);
        btnPickPhoto.setOnClickListener(listener);
        btnCancel.setOnClickListener(listener);
    }
}
