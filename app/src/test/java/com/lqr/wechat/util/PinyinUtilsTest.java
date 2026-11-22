package com.lqr.wechat.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PinyinUtilsTest {

    @Test
    public void getPinyin_convertsHanziAndSkipsWhitespace() {
        assertEquals("CANGXIAO", PinyinUtils.getPinyin("沧晓"));
        assertEquals("CANGXIAO", PinyinUtils.getPinyin("沧  晓"));
    }

    @Test
    public void getPinyin_keepsNonHanziChars() {
        assertEquals("CANGXIAOf5*&", PinyinUtils.getPinyin("沧晓f5*&"));
        assertEquals("ABC", PinyinUtils.getPinyin("ABC"));
    }

    @Test
    public void getPinyin_convertsBothEndsOfTheRange() {
        //U+4E00 and U+9FA5
        assertEquals("YIYUE", PinyinUtils.getPinyin("一龥"));
    }

    @Test
    public void getPinyin_usesTheFirstReadingOfAPolyphone() {
        assertEquals("DAN", PinyinUtils.getPinyin("单"));
    }

    @Test
    public void getPinyin_keepsHanziThatHasNoPinyin() {
        //pinyin4j returns null for some chars in the CJK range, which used to throw an NPE
        assertEquals("乄A", PinyinUtils.getPinyin("乄A"));
    }

    @Test
    public void getPinyin_handlesNullAndEmpty() {
        assertEquals("", PinyinUtils.getPinyin(null));
        assertEquals("", PinyinUtils.getPinyin(""));
    }
}
