package com.lqr.wechat.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @创建者 CSDN_LQR
 * @描述 应用版本信息
 */
public class Common {
    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode;
    }

    /**
     * 获取版本名
     *
     * @param context
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
}
