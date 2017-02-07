package com.lqr.wechat.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.lqr.wechat.AppConst;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @创建者 CSDN_LQR
 * @描述 短信广播接收者
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {
    private static MessageListener mMessageListener;

    public SMSBroadcastReceiver(MessageListener messageListener) {
        super();
        mMessageListener = messageListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AppConst.SMS_RECEIVED_ACTION)) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                //短信内容
                String content = smsMessage.getDisplayMessageBody();

                //过滤短信
                int a = content.indexOf("华谕咕饥");
                if (a > 0) {
                    //提取六位数字字符串
                    Pattern p = Pattern.compile("\\d{6}");
                    Matcher m = p.matcher(content);
                    m.find();
                    String code = m.group();
                    if (code != null && !code.equals("")) {
                        mMessageListener.onReceived(code);
                        //中断广播
                        abortBroadcast();
                    }
                }
            }
        }

    }

    //回调接口
    public interface MessageListener {
        void onReceived(String message);
    }

}
