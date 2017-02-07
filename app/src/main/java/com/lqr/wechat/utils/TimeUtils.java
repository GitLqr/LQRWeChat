package com.lqr.wechat.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;

/**
 * @创建者 CSDN_LQR
 * @描述 时间工具（需要joda-time）
 */
public class TimeUtils {

    /**
     * 得到仿微信日期格式输出
     *
     * @param msgTimeMillis
     * @return
     */
    public static String getMsgFormatTime(long msgTimeMillis) {
        DateTime nowTime = new DateTime();
        DateTime msgTime = new DateTime(msgTimeMillis);
        int days = Days.daysBetween(nowTime, msgTime).getDays();
        if (days < 1) {
            //早上、下午、晚上 1:40
            int hourOfDay = msgTime.getHourOfDay();
            String when;
            if (hourOfDay >= 18) {//18-24
                when = "晚上";
            } else if (hourOfDay >= 13) {//13-18
                when = "下午";
            } else if (hourOfDay >= 11) {//11-13
                when = "中午";
            } else if (hourOfDay >= 5) {//5-11
                when = "早上";
            } else {//0-5
                when = "凌晨";
            }
            return when + " " + msgTime.toString("hh:mm");
        } else if (days == 1) {
            //昨天
            return "昨天";
        } else if (days <= 7) {
            //星期
            switch (msgTime.getDayOfWeek()) {
                case DateTimeConstants.SUNDAY:
                    return "周日";
                case DateTimeConstants.MONDAY:
                    return "周一";
                case DateTimeConstants.TUESDAY:
                    return "周二";
                case DateTimeConstants.WEDNESDAY:
                    return "周三";
                case DateTimeConstants.THURSDAY:
                    return "周四";
                case DateTimeConstants.FRIDAY:
                    return "周五";
                case DateTimeConstants.SATURDAY:
                    return "周六";
            }
            return "";
        } else {
            //12月22日
            return msgTime.toString("MM月dd日");
        }
    }

}
