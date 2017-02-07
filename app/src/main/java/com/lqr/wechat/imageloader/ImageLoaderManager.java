package com.lqr.wechat.imageloader;

import android.net.Uri;
import android.widget.ImageView;

import com.lqr.wechat.App;

/**
 * @创建者 CSDN_LQR
 * @描述 图片加载管理(对universalimage工具的包装)
 */
public class ImageLoaderManager {

    public static void LoadNetImage(String imgUrl, ImageView imageView) {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(imgUrl, imageView, App.options);
    }

    public static void LoadLocalImage(String path, ImageView imageView) {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(Uri.parse("file://" + path).toString(), imageView, App.options);
    }


//    public static void LoadNetImage(String imgUrl, ImageView imageView) {
//        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(imgUrl, imageView);
//    }
//
//    public static void LoadNetImage(String imgUrl, ImageView imageView, DisplayImageOptions o) {
//        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(imgUrl, imageView, o);
//    }
//
//    public static void LoadLocalImage(String path, ImageView imageView) {
//        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(Uri.parse("file://" + path).toString(), imageView);
//    }
//
//    public static void LoadLocalImage(String path, ImageView imageView, DisplayImageOptions o) {
//        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(Uri.parse("file://" + path).toString(), imageView, o);
//    }
}