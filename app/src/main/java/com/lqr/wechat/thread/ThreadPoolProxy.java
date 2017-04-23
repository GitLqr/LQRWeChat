package com.lqr.wechat.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @创建者 CSDN_LQR
 * @描述 创建线程池，执行任务，提交任务
 */
public class ThreadPoolProxy {
    ThreadPoolExecutor mExecutor;// 只需创建一次
    int mCorePoolSize;
    int mMaximumPoolSize;
    long mKeepAliveTime;

    public ThreadPoolProxy(int corePoolSize, int maximumPoolSize,
                           long keepAliveTime) {
        super();
        mCorePoolSize = corePoolSize;
        mMaximumPoolSize = maximumPoolSize;
        mKeepAliveTime = keepAliveTime;
    }

    private ThreadPoolExecutor initThreadPoolExecutor() {// 双重检查加锁
        if (mExecutor == null) {
            synchronized (ThreadPoolProxy.class) {
                if (mExecutor == null) {
                    TimeUnit unit = TimeUnit.MILLISECONDS;// 毫秒
                    BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();// 无界队列
                    ThreadFactory threadFactory = Executors
                            .defaultThreadFactory();
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();// 任务数量超过时，丢弃任务，抛出异常

                    mExecutor = new ThreadPoolExecutor(//
                            mCorePoolSize, // 核心线程数
                            mMaximumPoolSize,// 最大线程数
                            mKeepAliveTime, // 保持时间
                            unit, // 保持时间对应的单位
                            workQueue,// 缓存队列/阻塞队列
                            threadFactory,// 线程工厂
                            handler// 异常捕获器
                    );
                }
            }

        }
        return mExecutor;
    }

    /**
     * 执行任务
     *
     * @param task
     */
    public void execute(Runnable task) {
        initThreadPoolExecutor();
        mExecutor.execute(task);
    }

    /**
     * 提交任务
     *
     * @param task
     */
    public Future<?> submit(Runnable task) {
        initThreadPoolExecutor();
        return mExecutor.submit(task);
    }

    /**
     * 移除任务
     *
     * @param task
     */
    public void removeTask(Runnable task) {
        initThreadPoolExecutor();
        mExecutor.remove(task);
    }
}
