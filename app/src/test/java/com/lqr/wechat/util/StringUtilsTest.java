package com.lqr.wechat.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

    //The old implementation looped forever on a truncated escape at the end, so every case has a timeout

    @Test(timeout = 1000)
    public void decodeUnicode_decodesEscapes() {
        assertEquals("你好", StringUtils.decodeUnicode("\\u4f60\\u597d"));
        assertEquals("a你b", StringUtils.decodeUnicode("a\\u4F60b"));
    }

    @Test(timeout = 1000)
    public void decodeUnicode_keepsTextWithoutEscapes() {
        assertEquals("", StringUtils.decodeUnicode(""));
        assertEquals("abc", StringUtils.decodeUnicode("abc"));
    }

    @Test(timeout = 1000)
    public void decodeUnicode_keepsTruncatedEscapeAtEnd() {
        assertEquals("abc\\u12", StringUtils.decodeUnicode("abc\\u12"));
        assertEquals("你\\u", StringUtils.decodeUnicode("\\u4f60\\u"));
    }

    @Test(timeout = 1000)
    public void decodeUnicode_keepsInvalidHexAsIs() {
        assertEquals("\\uzzzz你", StringUtils.decodeUnicode("\\uzzzz\\u4f60"));
        assertEquals("\\u你", StringUtils.decodeUnicode("\\u\\u4f60"));
        //Integer.parseInt would accept a leading sign, which is not a valid escape
        assertEquals("\\u+123", StringUtils.decodeUnicode("\\u+123"));
        assertEquals("\\u-123", StringUtils.decodeUnicode("\\u-123"));
        //Full-width digits are not ASCII hex digits
        assertEquals("\\u\uff11\uff12\uff13\uff14", StringUtils.decodeUnicode("\\u\uff11\uff12\uff13\uff14"));
    }
}
