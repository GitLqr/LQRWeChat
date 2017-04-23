/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package cn.sharesdk.onekeyshare.themes.classic;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mob.tools.utils.ResHelper;

/** 下拉刷新的头部控件  */
public class PRTHeader extends LinearLayout {
	private static final int DESIGN_SCREEN_WIDTH = 720;
	private static final int DESIGN_AVATAR_WIDTH = 64;
	private static final int DESIGN_AVATAR_PADDING = 24;

	private TextView tvHeader;
	private RotateImageView ivArrow;
	private ProgressBar pbRefreshing;

	public PRTHeader(Context context) {
		super(context);
		int[] size = ResHelper.getScreenSize(context);
		float screenWidth = size[0] < size[1] ? size[0] : size[1];
		float ratio = screenWidth / DESIGN_SCREEN_WIDTH;

		setOrientation(VERTICAL);

		LinearLayout llInner = new LinearLayout(context);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_HORIZONTAL;
		addView(llInner, lp);

		ivArrow = new RotateImageView(context);
		int resId = ResHelper.getBitmapRes(context, "ssdk_oks_ptr_ptr");
		if (resId > 0) {
			ivArrow.setImageResource(resId);
		}
		int avatarWidth = (int) (ratio * DESIGN_AVATAR_WIDTH);
		lp = new LayoutParams(avatarWidth, avatarWidth);
		lp.gravity = Gravity.CENTER_VERTICAL;
		int avataPadding = (int) (ratio * DESIGN_AVATAR_PADDING);
		lp.topMargin = lp.bottomMargin = avataPadding;
		llInner.addView(ivArrow, lp);

		pbRefreshing = new ProgressBar(context);
		resId = ResHelper.getBitmapRes(context, "ssdk_oks_classic_progressbar");
		Drawable pbdrawable = context.getResources().getDrawable(resId);
		pbRefreshing.setIndeterminateDrawable(pbdrawable);
		llInner.addView(pbRefreshing, lp);
		pbRefreshing.setVisibility(View.GONE);

		tvHeader = new TextView(getContext());
		tvHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tvHeader.setPadding(avataPadding, 0, avataPadding, 0);
		tvHeader.setTextColor(0xff09bb07);
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_VERTICAL;
		llInner.addView(tvHeader, lp);
	}

	public void onPullDown(int percent) {
		if (percent > 100) {
			int degree = (percent - 100) * 180 / 20;
			if (degree > 180) {
				degree = 180;
			}
			if (degree < 0) {
				degree = 0;
			}
			ivArrow.setRotation(degree);
		} else {
			ivArrow.setRotation(0);
		}

		if (percent < 100) {
			int resId = ResHelper.getStringRes(getContext(), "ssdk_oks_pull_to_refresh");
			if (resId > 0) {
				tvHeader.setText(resId);
			}
		} else {
			int resId = ResHelper.getStringRes(getContext(), "ssdk_oks_release_to_refresh");
			if (resId > 0) {
				tvHeader.setText(resId);
			}
		}
	}

	public void onRequest() {
		ivArrow.setVisibility(View.GONE);
		pbRefreshing.setVisibility(View.VISIBLE);
		int resId = ResHelper.getStringRes(getContext(), "ssdk_oks_refreshing");
		if (resId > 0) {
			tvHeader.setText(resId);
		}
	}

	public void reverse() {
		pbRefreshing.setVisibility(View.GONE);
		ivArrow.setRotation(180);
		ivArrow.setVisibility(View.VISIBLE);
	}

}
