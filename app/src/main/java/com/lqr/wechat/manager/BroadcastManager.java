package com.lqr.wechat.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import retrofit2.adapter.rxjava.HttpException;

/**
 * @创建者 CSDN_LQR
 * @描述 广播管理
 */
public class BroadcastManager {
    private Context mContext;
    private static BroadcastManager mInstance;
    private Map<String, BroadcastReceiver> mReceiverMap;

    private BroadcastManager(Context context) {
        mContext = context.getApplicationContext();
        mReceiverMap = new HashMap<>();
    }

    public static BroadcastManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BroadcastManager.class) {
                if (mInstance == null)
                    mInstance = new BroadcastManager(context);
            }
        }
        return mInstance;
    }

    /**
     * 添加
     */
    public void register(String action, BroadcastReceiver receiver) {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(action);
            mContext.registerReceiver(receiver, filter);
            mReceiverMap.put(action, receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送广播
     *
     * @param action 唯一码
     */
    public void sendBroadcast(String action) {
        sendBroadcast(action, "");
    }

    /**
     * 发送广播
     *
     * @param action 唯一码
     * @param obj    参数
     */
    public void sendBroadcastWithObjct(String action, Object obj) {
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            intent.putExtra("result", JsonMananger.beanToJson(obj));
            mContext.sendBroadcast(intent);
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }

    public void sendBroadcast(String action, Parcelable obj) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("result", obj);
        mContext.sendBroadcast(intent);
    }

    /**
     * 发送参数为 String 的数据广播
     *
     * @param action
     * @param s
     */
    public void sendBroadcast(String action, String s) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("String", s);
        mContext.sendBroadcast(intent);
    }


    /**
     * 销毁广播
     *
     * @param action
     */
    public void unregister(String action) {
        if (mReceiverMap != null) {
            BroadcastReceiver receiver = mReceiverMap.remove(action);
            if (receiver != null) {
                mContext.unregisterReceiver(receiver);
            }
        }
    }
}
