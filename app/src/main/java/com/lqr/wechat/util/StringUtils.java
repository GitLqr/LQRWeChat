package com.lqr.wechat.util;

import java.util.List;

/**
 * @创建者 GitLqr
 * @描述 字符串工具类
 */
public class StringUtils {
    /**
     * 得到不为空的字符串
     *
     * @param o
     * @return
     */
    public static String getNotNULLStr(Object o) {
        return o == null ? "" : o.toString();
    }

    /**
     * 判断字符串或集合是否为空
     *
     * @param o
     * @return
     */
    public static boolean isEmpty(Object o) {
        if (o instanceof List)
            return ((List) o).size() == 0;
        return o == null || o.toString().equals("");
    }

    /**
     * 判断字符串是否为空（空格字符串也是blank）
     *
     * @param s
     * @return
     */
    public static boolean isBlank(final CharSequence s) {
        if (s == null) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 函数传入汉字的Unicode编码字符串，返回相应的汉字字符串
     *
     * @return
     */
    public static String decodeUnicode(final String utfString) {
        StringBuilder sb = new StringBuilder();
        int i;
        int pos = 0;

        while ((i = utfString.indexOf("\\u", pos)) != -1) {
            sb.append(utfString, pos, i);
            if (i + 6 > utfString.length()) {
                //Truncated escape at the end: keep it as is, otherwise pos never advances
                pos = i;
                break;
            }
            int code = parseHex4(utfString, i + 2);
            if (code < 0) {
                //Keep only these 2 chars as is, so an escape right after them can still be decoded
                sb.append("\\u");
                pos = i + 2;
            } else {
                sb.append((char) code);
                pos = i + 6;
            }
        }
        sb.append(utfString, pos, utfString.length());

        return sb.toString();
    }

    /**
     * Parse 4 ASCII hex digits starting at start, or return -1 if any of them is not one
     * (Integer.parseInt and Character.digit also accept a sign or non-ASCII digits)
     */
    private static int parseHex4(String s, int start) {
        int code = 0;
        for (int k = start; k < start + 4; k++) {
            char c = s.charAt(k);
            int digit;
            if (c >= '0' && c <= '9') {
                digit = c - '0';
            } else if (c >= 'a' && c <= 'f') {
                digit = c - 'a' + 10;
            } else if (c >= 'A' && c <= 'F') {
                digit = c - 'A' + 10;
            } else {
                return -1;
            }
            code = code * 16 + digit;
        }
        return code;
    }

}