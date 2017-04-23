/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package cn.sharesdk.onekeyshare.themes.classic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sharesdk.onekeyshare.themes.classic.FriendAdapter.Following;

import com.mob.tools.gui.AsyncImageView;
import com.mob.tools.gui.BitmapProcessor;
import com.mob.tools.utils.ResHelper;

/** 好友列表的item */
public class FriendListItem extends LinearLayout {
	private static final int DESIGN_AVATAR_WIDTH = 64;
	private static final int DESIGN_AVATAR_PADDING = 24;
	private static final int DESIGN_ITEM_HEIGHT = 96;
	private static final int DESIGN_ITEM_PADDING = 20;

	private ImageView ivCheck;
	private AsyncImageView aivIcon;
	private TextView tvName;
	/** 好友列表中，被选中的checkbox图标 */
	private Bitmap bmChd;
	/** 好友列表中，没选中的checkbox图标 */
	private Bitmap bmUnch;

	public FriendListItem(Context context, float ratio) {
		super(context);
		int itemPadding = (int) (ratio * DESIGN_ITEM_PADDING);
		setPadding(itemPadding, 0, itemPadding, 0);
		setMinimumHeight((int) (ratio * DESIGN_ITEM_HEIGHT));
		setBackgroundColor(0xffffffff);

		ivCheck = new ImageView(context);
		LayoutParams lp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_VERTICAL;
		addView(ivCheck, lp);

		aivIcon = new AsyncImageView(context);
		int avatarWidth = (int) (ratio * DESIGN_AVATAR_WIDTH);
		lp = new LayoutParams(avatarWidth, avatarWidth);
		lp.gravity = Gravity.CENTER_VERTICAL;
		int avatarMargin = (int) (ratio * DESIGN_AVATAR_PADDING);
		lp.setMargins(avatarMargin, 0, avatarMargin, 0);
		addView(aivIcon, lp);

		tvName = new TextView(context);
		tvName.setTextColor(0xff000000);
		tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tvName.setSingleLine();
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_VERTICAL;
		lp.weight = 1;
		addView(tvName, lp);

		int resId = ResHelper.getBitmapRes(context, "ssdk_oks_classic_check_checked");
		if (resId > 0) {
			bmChd = BitmapFactory.decodeResource(context.getResources(), resId);
		}
		resId = ResHelper.getBitmapRes(getContext(), "ssdk_oks_classic_check_default");
		if (resId > 0) {
			bmUnch = BitmapFactory.decodeResource(context.getResources(), resId);
		}
	}

	public void update(Following following, boolean fling) {
		tvName.setText(following.screenName);
		ivCheck.setImageBitmap(following.checked ? bmChd : bmUnch);
		if (aivIcon != null) {
			if (fling) {
				Bitmap bm = BitmapProcessor.getBitmapFromCache(following.icon);
				if (bm != null && !bm.isRecycled()) {
					aivIcon.setImageBitmap(bm);
				} else {
					aivIcon.execute(null, 0);
				}
			} else {
				aivIcon.execute(following.icon, 0);
			}
		}
	}

}
