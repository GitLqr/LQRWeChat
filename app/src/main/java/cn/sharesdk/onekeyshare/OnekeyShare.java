/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package cn.sharesdk.onekeyshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.mob.MobSDK;
import com.mob.tools.utils.BitmapHelper;
import com.mob.tools.utils.ResHelper;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

/**
* 快捷分享的入口
* <p>
* 通过不同的setter设置参数，然后调用{@link #show(Context)}方法启动快捷分享
*/
public class OnekeyShare {
	private HashMap<String, Object> params;

	public OnekeyShare() {
		params = new HashMap<String, Object>();
		params.put("customers", new ArrayList<CustomerLogo>());
		params.put("hiddenPlatforms", new HashMap<String, String>());
	}

	/** address是接收人地址，仅在信息和邮件使用，否则可以不提供 */
	public void setAddress(String address) {
		params.put("address", address);
	}

	/**
	 * title标题，在印象笔记、邮箱、信息、微信（包括好友、朋友圈和收藏）、
	 * 易信（包括好友、朋友圈）、人人网和QQ空间使用，否则可以不提供
	 */
	public void setTitle(String title) {
		params.put("title", title);
	}

	/** titleUrl是标题的网络链接，仅在人人网和QQ空间使用，否则可以不提供 */
	public void setTitleUrl(String titleUrl) {
		params.put("titleUrl", titleUrl);
	}

	/** text是分享文本，所有平台都需要这个字段 */
	public void setText(String text) {
		params.put("text", text);
	}

	/** 获取text字段的值 */
	public String getText() {
		return params.containsKey("text") ? String.valueOf(params.get("text")) : null;
	}

	/** imagePath是本地的图片路径，除Linked-In外的所有平台都支持这个字段 */
	public void setImagePath(String imagePath) {
		if(!TextUtils.isEmpty(imagePath)) {
			params.put("imagePath", imagePath);
		}
	}

	/** imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段 */
	public void setImageUrl(String imageUrl) {
		if (!TextUtils.isEmpty(imageUrl)) {
			params.put("imageUrl", imageUrl);
		}
	}

	/** url在微信（包括好友、朋友圈收藏）和易信（包括好友和朋友圈）中使用，否则可以不提供 */
	public void setUrl(String url) {
		params.put("url", url);
	}

	/** filePath是待分享应用程序的本地路劲，仅在微信（易信）好友和Dropbox中使用，否则可以不提供 */
	public void setFilePath(String filePath) {
		params.put("filePath", filePath);
	}

	/** comment是我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供 */
	public void setComment(String comment) {
		params.put("comment", comment);
	}

	/** site是分享此内容的网站名称，仅在QQ空间使用，否则可以不提供 */
	public void setSite(String site) {
		params.put("site", site);
	}

	/** siteUrl是分享此内容的网站地址，仅在QQ空间使用，否则可以不提供 */
	public void setSiteUrl(String siteUrl) {
		params.put("siteUrl", siteUrl);
	}

	/** foursquare分享时的地方名 */
	public void setVenueName(String venueName) {
		params.put("venueName", venueName);
	}

	/** foursquare分享时的地方描述 */
	public void setVenueDescription(String venueDescription) {
		params.put("venueDescription", venueDescription);
	}

	/** 分享地纬度，新浪微博、腾讯微博和foursquare支持此字段 */
	public void setLatitude(float latitude) {
		params.put("latitude", latitude);
	}

	/** 分享地经度，新浪微博、腾讯微博和foursquare支持此字段 */
	public void setLongitude(float longitude) {
		params.put("longitude", longitude);
	}

	/** 是否直接分享 */
	public void setSilent(boolean silent) {
		params.put("silent", silent);
	}

	/** 设置编辑页的初始化选中平台 */
	public void setPlatform(String platform) {
		params.put("platform", platform);
	}

	/** 设置KakaoTalk的应用下载地址 */
	public void setInstallUrl(String installurl) {
		params.put("installurl", installurl);
	}

	/** 设置KakaoTalk的应用打开地址 */
	public void setExecuteUrl(String executeurl) {
		params.put("executeurl", executeurl);
	}

	/** 设置微信分享的音乐的地址 */
	public void setMusicUrl(String musicUrl) {
		params.put("musicUrl", musicUrl);
	}

	/** 设置自定义的外部回调 */
	public void setCallback(PlatformActionListener callback) {
		params.put("callback", callback);
	}

	/** 返回操作回调 */
	public PlatformActionListener getCallback() {
		return ResHelper.forceCast(params.get("callback"));
	}

	/** 设置用于分享过程中，根据不同平台自定义分享内容的回调 */
	public void setShareContentCustomizeCallback(ShareContentCustomizeCallback callback) {
		params.put("customizeCallback", callback);
	}

	/** 自定义不同平台分享不同内容的回调 */
	public ShareContentCustomizeCallback getShareContentCustomizeCallback() {
		return ResHelper.forceCast(params.get("customizeCallback"));
	}

	/** 设置自己图标和点击事件，可以重复调用添加多次 */
	public void setCustomerLogo(Bitmap logo, String label, OnClickListener ocl) {
		CustomerLogo cl = new CustomerLogo();
		cl.logo = logo;
		cl.label = label;
		cl.listener = ocl;
		ArrayList<CustomerLogo> customers = ResHelper.forceCast(params.get("customers"));
		customers.add(cl);
	}

	/** 设置一个总开关，用于在分享前若需要授权，则禁用sso功能 */
	public void disableSSOWhenAuthorize() {
		params.put("disableSSO", true);
	}

	/** 设置视频网络地址 */
	public void setVideoUrl(String url) {
		params.put("url", url);
		params.put("shareType", Platform.SHARE_VIDEO);
	}

	/** 设置编辑页面的显示模式为Dialog模式 */
	@Deprecated
	public void setDialogMode() {
		params.put("dialogMode", true);
	}

	/** 添加一个隐藏的platform */
	public void addHiddenPlatform(String platform) {
		HashMap<String, String> hiddenPlatforms = ResHelper.forceCast(params.get("hiddenPlatforms"));
		hiddenPlatforms.put(platform, platform);
	}

	/** 设置一个将被截图分享的View , surfaceView是截不了图片的*/
	public void setViewToShare(View viewToShare) {
		try {
			Bitmap bm = BitmapHelper.captureView(viewToShare, viewToShare.getWidth(), viewToShare.getHeight());
			params.put("viewToShare", bm);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/** 腾讯微博分享多张图片 */
	public void setImageArray(String[] imageArray) {
		params.put("imageArray", imageArray);
	}

	/** 设置在执行分享到QQ或QZone的同时，分享相同的内容腾讯微博 */
	public void setShareToTencentWeiboWhenPerformingQQOrQZoneSharing() {
		params.put("isShareTencentWeibo", true);
	}

	/** 设置分享界面的样式，目前只有一种，不需要设置 */
	public void setTheme(OnekeyShareTheme theme) {
		params.put("theme", theme.getValue());
	}

	@SuppressWarnings("unchecked")
	public void show(Context context) {
		HashMap<String, Object> shareParamsMap = new HashMap<String, Object>();
		shareParamsMap.putAll(params);

		MobSDK.init(context.getApplicationContext());
		ShareSDK.initSDK(context);

		// 打开分享菜单的统计
		ShareSDK.logDemoEvent(1, null);

		int iTheme = 0;
		try {
			iTheme = ResHelper.parseInt(String.valueOf(shareParamsMap.remove("theme")));
		} catch (Throwable t) {}
		OnekeyShareTheme theme = OnekeyShareTheme.fromValue(iTheme);
		OnekeyShareThemeImpl themeImpl = theme.getImpl();

		themeImpl.setShareParamsMap(shareParamsMap);
		themeImpl.setDialogMode(shareParamsMap.containsKey("dialogMode") ? ((Boolean) shareParamsMap.remove("dialogMode")) : false);
		themeImpl.setSilent(shareParamsMap.containsKey("silent") ? ((Boolean) shareParamsMap.remove("silent")) : false);
		themeImpl.setCustomerLogos((ArrayList<CustomerLogo>) shareParamsMap.remove("customers"));
		themeImpl.setHiddenPlatforms((HashMap<String, String>) shareParamsMap.remove("hiddenPlatforms"));
		themeImpl.setPlatformActionListener((PlatformActionListener) shareParamsMap.remove("callback"));
		themeImpl.setShareContentCustomizeCallback((ShareContentCustomizeCallback) shareParamsMap.remove("customizeCallback"));
		if (shareParamsMap.containsKey("disableSSO") ? ((Boolean) shareParamsMap.remove("disableSSO")) : false) {
			themeImpl.disableSSO();
		}

		themeImpl.show(context.getApplicationContext());
	}

}
