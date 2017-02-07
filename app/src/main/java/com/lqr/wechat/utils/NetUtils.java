package com.lqr.wechat.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

/**
 * @创建者 CSDN_LQR
 * @描述 跟网络相关的工具类
 */
public class NetUtils {
    private NetUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断当前网络是否可以上网并吐司提醒
     *
     * @param context
     * @return
     */
    public static boolean isConnectedAndToast(Context context) {
        boolean flag = isConnected(context);
        if (!flag) {
            UIUtils.showToast("请检查网络状态");
        }
        return flag;
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断网络是否稳定(需要子线程)
     *
     * @return
     */
    public static boolean isNetStable() {
        boolean isStable = false;

        try {
            /*
             * ping -c 3 -w 100 中 ， -c 是指ping的次数 3是指ping 3次 ， -w
			 * 100以秒为单位指定超时间隔，是指超时时间为100秒
			 */
            Process process = Runtime.getRuntime().exec(
                    "ping -c 3 -w 5 192.168.43.1");
            int status = process.waitFor();

            if (status == 0) {
                // 连接正常
                isStable = true;
            } else {
                // 连接异常
                // Fail:Host unreachable
                isStable = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return isStable;
    }


    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;
        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

}  