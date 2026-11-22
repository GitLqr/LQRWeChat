package com.lqr.wechat.util;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;

/**
 * @创建者 GitLqr
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
        return getMsgFormatTime(msgTimeMillis, System.currentTimeMillis());
    }

    /**
     * @param nowMillis passed in so the result can be unit tested
     */
    public static String getMsgFormatTime(long msgTimeMillis, long nowMillis) {
        DateTime msgTime = new DateTime(msgTimeMillis);
        DateTime nowTime = new DateTime(nowMillis);
        //Compare calendar dates: Days.daysBetween on DateTime counts whole 24-hour periods,
        //so 23:00 yesterday would still be "today" at 01:00
        int days = Days.daysBetween(msgTime.toLocalDate(), nowTime.toLocalDate()).getDays();
        if (days == 0) {
            //早上、下午、晚上 1:40
            return getTime(msgTime);
        } else if (days == 1) {
            //昨天
            return "昨天 " + getTime(msgTime);
        } else if (days > 1 && days < 7) {
            //Weekday, but not for 7 days ago, which would repeat today's weekday
            switch (msgTime.getDayOfWeek()) {
                case DateTimeConstants.SUNDAY:
                    return "周日 " + getTime(msgTime);
                case DateTimeConstants.MONDAY:
                    return "周一 " + getTime(msgTime);
                case DateTimeConstants.TUESDAY:
                    return "周二 " + getTime(msgTime);
                case DateTimeConstants.WEDNESDAY:
                    return "周三 " + getTime(msgTime);
                case DateTimeConstants.THURSDAY:
                    return "周四 " + getTime(msgTime);
                case DateTimeConstants.FRIDAY:
                    return "周五 " + getTime(msgTime);
                case DateTimeConstants.SATURDAY:
                    return "周六 " + getTime(msgTime);
            }
            return "";
        } else {
            //12月22日
            //A future date (the sender's clock is ahead) also lands here instead of showing "yesterday"
            String datePattern = msgTime.getYear() == nowTime.getYear() ? "MM月dd日" : "yyyy年MM月dd日";
            return msgTime.toString(datePattern) + " " + getTime(msgTime);
        }
    }

    @NonNull
    private static String getTime(DateTime msgTime) {
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
    }

}
