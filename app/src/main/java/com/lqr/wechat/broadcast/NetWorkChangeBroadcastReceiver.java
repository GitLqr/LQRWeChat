package com.lqr.wechat.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.lqr.wechat.AppConst;


/**
 * @创建者 CSDN_LQR
 * @描述 网络变更广播接收者
 */
public class NetWorkChangeBroadcastReceiver extends BroadcastReceiver {
    private NetWorkChangeListener mNetWorkChangeListener;

    public NetWorkChangeBroadcastReceiver(NetWorkChangeListener netWorkChangeListener) {
        super();
        this.mNetWorkChangeListener = netWorkChangeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AppConst.NETWORK_CHANGE_RECEIVED_ACTION)) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                for (int i = 0; i < networkInfos.length; i++) {
                    NetworkInfo.State state = networkInfos[i].getState();
                    //发现可以联网就不再判断
                    if (NetworkInfo.State.CONNECTED == state) {
                        mNetWorkChangeListener.onReceived();
                        return;
                    }
                }
            }
        }
    }

    /**
     * 接口回调
     */
    public interface NetWorkChangeListener {
        void onReceived();
    }

}