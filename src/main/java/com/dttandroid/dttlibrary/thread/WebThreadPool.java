package com.dttandroid.dttlibrary.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: lufengwen
 * @Date: 2015年6月30日 下午11:02:37
 * @Description:
 */
public class WebThreadPool {
    private static Object sLock = new Object();
    private static ExecutorService sExecutor;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "WebRequestThreadPool(CachedThreadPool) Thread #" + mCount.getAndIncrement());
        }
    };

    /**
     * 提交Web请求任务
     * @param task
     */
    /* package */ static void submit(Runnable task) {
        if (task != null) {
            getThreadPool().submit(task);
        }
    }

    /**
     * 线程缓存的线程池，60秒无请求自动关闭等待的缓存线程
     * @return 线程池
     */
    public static ExecutorService getThreadPool() {
        if (sExecutor == null) {
            synchronized (sLock) {
                if (sExecutor == null) {
                    sExecutor = Executors.newCachedThreadPool(sThreadFactory);
                }
            }
        }
        return sExecutor;
    }
}
