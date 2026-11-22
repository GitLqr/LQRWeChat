package com.lqr.wechat.util;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeUtilsTest {

    //Friday
    private static final long NOW = millis(2026, 11, 20, 1, 0);

    private static long millis(int year, int month, int day, int hour, int minute) {
        return new DateTime(year, month, day, hour, minute).getMillis();
    }

    private static String format(long msgTimeMillis) {
        return TimeUtils.getMsgFormatTime(msgTimeMillis, NOW);
    }

    @Test
    public void sameDay_showsTimeOnly() {
        assertEquals("凌晨 01:00", format(millis(2026, 11, 20, 1, 0)));
    }

    @Test
    public void previousCalendarDay_isYesterday_evenWithinTwentyFourHours() {
        //Only 2 hours ago, but on the previous calendar day
        assertEquals("昨天 晚上 11:00", format(millis(2026, 11, 19, 23, 0)));
        assertEquals("昨天 凌晨 02:00", format(millis(2026, 11, 19, 2, 0)));
    }

    @Test
    public void withinTheLastWeek_showsWeekday() {
        assertEquals("周三 下午 03:00", format(millis(2026, 11, 18, 15, 0)));
        assertEquals("周六 早上 09:30", format(millis(2026, 11, 14, 9, 30)));
    }

    @Test
    public void sevenDaysAgo_showsDate_becauseTheWeekdayWouldBeTodays() {
        assertEquals("11月13日 晚上 08:00", format(millis(2026, 11, 13, 20, 0)));
    }

    @Test
    public void earlierThisYear_showsMonthAndDay() {
        assertEquals("03月05日 中午 12:10", format(millis(2026, 3, 5, 12, 10)));
    }

    @Test
    public void previousYear_showsYear() {
        assertEquals("2025年12月30日 晚上 10:00", format(millis(2025, 12, 30, 22, 0)));
    }

    @Test
    public void lastWeekAcrossNewYear_stillShowsWeekday() {
        //Wednesday, 3 days before Saturday 2027-01-02
        assertEquals("周三 晚上 09:00", TimeUtils.getMsgFormatTime(millis(2026, 12, 30, 21, 0), millis(2027, 1, 2, 10, 0)));
    }

    @Test
    public void futureDay_showsDate_insteadOfYesterday() {
        //A message clock ahead of ours used to be shown as yesterday because of Math.abs
        assertEquals("11月21日 早上 08:00", format(millis(2026, 11, 21, 8, 0)));
    }
}
