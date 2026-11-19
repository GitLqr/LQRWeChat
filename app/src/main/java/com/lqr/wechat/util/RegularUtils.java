package com.lqr.wechat.util;

import java.util.regex.Pattern;

/**
 * @创建者 GitLqr
 * @描述 正则工具类
 */

public class RegularUtils {

    //Any 11-digit number starting with 13-19; carriers keep adding new prefixes, so do not list them one by one
    private static final Pattern MOBILE_PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

    public static boolean isMobile(String phoneNumber) {
        return phoneNumber != null && MOBILE_PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    public static boolean isEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
