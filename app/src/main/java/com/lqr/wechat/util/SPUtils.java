package com.lqr.wechat.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * @创建者 GitLqr
 * @描述 sharedPreferences工具类(单例模式)
 * Every write uses a fresh editor (a shared one would also flush other callers' pending edits),
 * and apply() writes to disk in the background instead of blocking the caller
 */
public class SPUtils {
    private static final String SP_NAME = "common";
    private static SPUtils mSpUtils;
    private final SharedPreferences sp;

    private SPUtils(Context context) {
        //The singleton keeps only the application context, so a passed-in Activity is not leaked.
        //MODE_APPEND is a file open mode, not a SharedPreferences mode; the file name stays the same
        sp = context.getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static SPUtils getInstance(Context context) {

        if (mSpUtils == null) {
            synchronized (SPUtils.class) {
                if (mSpUtils == null) {
                    mSpUtils = new SPUtils(context);
                    return mSpUtils;
                }
            }
        }

        return mSpUtils;

    }

    public void putBoolean(String key, Boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, Boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public void putString(String key, String value) {
        if (key == null) {
            return;
        }
        sp.edit().putString(key, value).apply();
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public void putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public Map<String, ?> getAll() {
        return sp.getAll();
    }

    public void remove(String key) {
        sp.edit().remove(key).apply();
    }

}