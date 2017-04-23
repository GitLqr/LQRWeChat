package com.lqr.wechat.thread;


/**
 * @创建者 CSDN_LQR
 * @描述 线程池工厂
 */
public class ThreadPoolFactory {

    static ThreadPoolProxy mNormalPool;
    static ThreadPoolProxy mDownLoadPool;

    /**
     * 得到一个普通的线程池
     *
     * @return
     */
    public static ThreadPoolProxy getNormalPool() {
        if (mNormalPool == null) {
            synchronized (ThreadPoolFactory.class) {
                if (mNormalPool == null) {
                    mNormalPool = new ThreadPoolProxy(5, 5, 3000);
                }
            }
        }
        return mNormalPool;
    }

    /**
     * 得到一个下载的线程池
     *
     * @return
     */
    public static ThreadPoolProxy getDownLoadPool() {
        if (mDownLoadPool == null) {
            synchronized (ThreadPoolFactory.class) {
                if (mDownLoadPool == null) {
                    mDownLoadPool = new ThreadPoolProxy(3, 3, 3000);
                }
            }
        }
        return mDownLoadPool;
    }
}
