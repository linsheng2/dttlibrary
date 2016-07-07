package com.dttandroid.dttlibrary.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: lufengwen
 * @Date: 2015年6月28日 上午11:03:42
 * @Description: 图片线程池
 */
public class PictureThreadPool {
	private static final int POOL_SIZE = 5;
	private static final int BLOCKING_QUEUE_SIZE = 15;

	private static Executor sExecutor;

	private static final ThreadFactory sParalleThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "PictureThreadPool Thread #" + mCount.getAndIncrement());
		}
	};

	/**
	 * 线程缓存的线程池，60秒无请求自动关闭等待的缓存线程
	 * @return 线程池
	 */
	public static Executor getExecutor() {
		if (sExecutor == null) {
			synchronized (PictureThreadPool.class) {
				if (sExecutor == null) {
					sExecutor = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(BLOCKING_QUEUE_SIZE), sParalleThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
				}
			}
		}
		return sExecutor;
	}
}
