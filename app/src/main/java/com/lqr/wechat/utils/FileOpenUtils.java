package com.lqr.wechat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * @创建者 CSDN_LQR
 * @创建时间 2016-4-6 上午8:56:26
 * @描述 文件打开工具类
 */
public class FileOpenUtils {

    /**
     * 调用自带的视频播放器
     *
     * @param context
     * @param path
     */
    public static void openVideo(Context context, String path) {
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "video/mp4");// "video/mp4"
        context.startActivity(intent);
    }

    /**
     * 调用自带的音频播放器
     *
     * @param context
     * @param path
     */
    public static void openAudio(Context context, String path) {
        File f = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(f), "audio/*");// "audio/mp3"
        context.startActivity(intent);
    }

    /**
     * 调用自带的图库
     *
     * @param context
     * @param path
     */
    public static void openPic(Context context, String path) {
        File f = new File(path);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(f), "image/*");
        context.startActivity(intent);
    }

    /**
     * 调用手机上能打开对应类型文件的程序
     *
     * @param context
     * @param path
     * @return true表示成功找到程序，false表示找不到能成功打开的程序
     */
    public static boolean openFile(Context context, String path) {
        String mimeType = MimeTypeUtils.getMimeType(path);
        File f = new File(path);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(f), mimeType);
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void openFile(Context context, String path, String mimeType) {
        File f = new File(path);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(f), mimeType);
        context.startActivity(intent);
    }

}
