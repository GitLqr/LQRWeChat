/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package cn.sharesdk.onekeyshare.themes.classic;

import java.util.ArrayList;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.CustomerLogo;

import com.mob.tools.gui.ViewPagerAdapter;
import com.mob.tools.utils.ResHelper;

/** 九宫格的适配器抽象类 */
public abstract class PlatformPageAdapter extends ViewPagerAdapter implements OnClickListener {
	/** 1秒内多次点击九格宫内的图标无效 */
	protected static final int MIN_CLICK_INTERVAL = 1000;
	public static final int DESIGN_BOTTOM_HEIGHT = 52;

	/** 九格宫内图标排列的二维数组，一维对应平台，二维对应页数 */
	protected Object[][] cells;
	private PlatformPage page;
	private IndicatorView vInd;

	protected int bottomHeight;
	protected int panelHeight;
	protected int cellHeight;
	protected int lineSize;
	protected int sepLineWidth;
	protected int paddingTop;
	protected int logoHeight;

	private long lastClickTime;

	public PlatformPageAdapter(PlatformPage page, ArrayList<Object> cells) {
		this.page = page;
		if (cells != null && !cells.isEmpty()) {
			calculateSize(page.getContext(), cells);
			collectCells(cells);
		}
	}

	/** 计算九宫格的格数，行数，格高，行高，图标大小 */
	protected abstract void calculateSize(Context context, ArrayList<Object> plats);

	/** 计算九宫格的格数，行数 */
	protected abstract void collectCells(ArrayList<Object> plats);

	public int getBottomHeight() {
		return bottomHeight;
	}

	public int getPanelHeight() {
		return panelHeight;
	}

	public int getCount() {
		return cells == null ? 0 : cells.length;
	}

	public void setIndicator(IndicatorView view) {
		vInd = view;
	}

	public void onScreenChange(int currentScreen, int lastScreen) {
		if (vInd != null) {
			vInd.setScreenCount(getCount());
			vInd.onScreenChange(currentScreen, lastScreen);
		}
	}

	public View getView(int index, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = createPanel(parent.getContext());
		}

		LinearLayout llPanel = ResHelper.forceCast(convertView);
		LinearLayout[] llCells = ResHelper.forceCast(llPanel.getTag());
		refreshPanel(llCells, cells[index]);
		return convertView;
	}

	private View createPanel(Context context) {
		LinearLayout llPanel = new LinearLayout(context);
		llPanel.setOrientation(LinearLayout.VERTICAL);
		llPanel.setBackgroundColor(0xfff2f2f2);

		int lineCount = panelHeight / cellHeight;
		LinearLayout[] llCells = new LinearLayout[lineCount * lineSize];
		llPanel.setTag(llCells);
		int cellBack = ResHelper.getBitmapRes(context, "ssdk_oks_classic_platform_cell_back");
		LinearLayout.LayoutParams lp;
		for (int i = 0; i < lineCount; i++) {
			LinearLayout llLine = new LinearLayout(context);
			lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, cellHeight);
			llPanel.addView(llLine, lp);

			for (int j = 0; j < lineSize; j++) {
				llCells[i * lineSize + j] = new LinearLayout(context);
				llCells[i * lineSize + j].setBackgroundResource(cellBack);
				llCells[i * lineSize + j].setOrientation(LinearLayout.VERTICAL);
				lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, cellHeight);
				lp.weight = 1;
				llLine.addView(llCells[i * lineSize + j], lp);

				if (j < lineSize - 1) {
					View vSep = new View(context);
					lp = new LinearLayout.LayoutParams(sepLineWidth, LayoutParams.MATCH_PARENT);
					llLine.addView(vSep, lp);
				}
			}

			View vSep = new View(context);
			lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, sepLineWidth);
			llPanel.addView(vSep, lp);
		}

		for (LinearLayout llCell : llCells) {
			ImageView ivLogo = new ImageView(context);
			ivLogo.setScaleType(ScaleType.CENTER_INSIDE);
			lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, logoHeight);
			lp.topMargin = paddingTop;
			llCell.addView(ivLogo, lp);

			TextView tvName = new TextView(context);
			tvName.setTextColor(0xff646464);
			tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			tvName.setGravity(Gravity.CENTER);
			lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			lp.weight = 1;
			llCell.addView(tvName, lp);
		}

		return llPanel;
	}

	private void refreshPanel(LinearLayout[] llCells, Object[] logos) {
		int cellBack = ResHelper.getBitmapRes(page.getContext(), "ssdk_oks_classic_platform_cell_back");
		int disableBack = ResHelper.getBitmapRes(page.getContext(), "ssdk_oks_classic_platfrom_cell_back_nor");
		for (int i = 0; i < logos.length; i++) {
			ImageView ivLogo = ResHelper.forceCast(llCells[i].getChildAt(0));
			TextView tvName = ResHelper.forceCast(llCells[i].getChildAt(1));
			if (logos[i] == null) {
				ivLogo.setVisibility(View.INVISIBLE);
				tvName.setVisibility(View.INVISIBLE);
				llCells[i].setBackgroundResource(disableBack);
				llCells[i].setOnClickListener(null);
			} else {
				ivLogo.setVisibility(View.VISIBLE);
				tvName.setVisibility(View.VISIBLE);
				ivLogo.requestLayout();
				tvName.requestLayout();
				llCells[i].setBackgroundResource(cellBack);
				llCells[i].setOnClickListener(this);
				llCells[i].setTag(logos[i]);

				if (logos[i] instanceof CustomerLogo) {
					CustomerLogo logo = ResHelper.forceCast(logos[i]);
					if (logo.logo != null) {
						ivLogo.setImageBitmap(logo.logo);
					} else {
						ivLogo.setImageBitmap(null);
					}
					if (logo.label != null) {
						tvName.setText(logo.label);
					} else {
						tvName.setText("");
					}
				} else {
					Platform plat = ResHelper.forceCast(logos[i]);
					String name = plat.getName().toLowerCase();
					int resId = ResHelper.getBitmapRes(ivLogo.getContext(),"ssdk_oks_classic_" + name);
					if (resId > 0) {
						ivLogo.setImageResource(resId);
					} else {
						ivLogo.setImageBitmap(null);
					}
					resId = ResHelper.getStringRes(tvName.getContext(), "ssdk_" + name);
					if (resId > 0) {
						tvName.setText(resId);
					} else {
						tvName.setText("");
					}
				}
			}
		}
	}

	public void onClick(View v) {
		long time = System.currentTimeMillis();
		if (time - lastClickTime < MIN_CLICK_INTERVAL) {
			return;
		}
		lastClickTime = time;

		if (v.getTag() instanceof CustomerLogo) {
			CustomerLogo logo = ResHelper.forceCast(v.getTag());
			page.performCustomLogoClick(v, logo);
		} else {
			Platform plat = ResHelper.forceCast(v.getTag());
			page.showEditPage(plat);
		}
	}

}
