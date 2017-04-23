package com.lqr.wechat.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * @创建者 CSDN_LQR
 * @描述 IO流工具类
 */
public class IOUtils {
    /**
     * 关闭流
     */
    public static boolean close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                LogUtils.e(e);
            }
        }
        return true;
    }
}