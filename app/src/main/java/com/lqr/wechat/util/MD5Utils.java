package com.lqr.wechat.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @创建者 CSDN_LQR
 * @描述 MD5加密工具
 */
public class MD5Utils {

    /**
     * md5加密(16位 小写)
     *
     * @param password
     * @return
     */
    public static String decode16(String password) {
        return decode32(password).substring(8, 24);
    }

    /**
     * md5加密(32位 小写)
     *
     * @param password
     * @return
     */
    public static String decode32(String password) {

        try {
            // 得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(password.getBytes());
            StringBuffer buffer = new StringBuffer();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                // System.out.println(str);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }

            // 标准的md5加密后的结果
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

    }
    /**
     * md5加密(32位 小写)
     *
     * @param password
     * @return
     */
//    public final static String getMessageDigest(String password) {
//        byte[] buffer = password.getBytes();
//        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
//                'a', 'b', 'c', 'd', 'e', 'f'};
//        try {
//            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
//            mdTemp.update(buffer);
//            byte[] md = mdTemp.digest();
//            int j = md.length;
//            char str[] = new char[j * 2];
//            int k = 0;
//            for (int i = 0; i < j; i++) {
//                byte byte0 = md[i];
//                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
//                str[k++] = hexDigits[byte0 & 0xf];
//            }
//            return new String(str);
//        } catch (Exception e) {
//            return null;
//        }
//    }
}
