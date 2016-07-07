package com.dttandroid.dttlibrary.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: lufengwen
 * @Date: 2015年6月22日 上午10:55:36
 * @Description: 定时线程池，执行定时任务时使用
 */
public class ScheduledThreadPool {
	private static final int POOL_SIZE = 1;

	private static Object sLock = new Object();
	private static ScheduledExecutorService sExecutor;

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "ScheduledThreadPool(1 ScheduledThreadPool) Thread #" + mCount.getAndIncrement());
		}
	};

	/* package */static void submit(Runnable task, long delay, TimeUnit unit) {
		if (task != null) {
			getThreadPool().schedule(task, delay, unit);
		}
	}

	/* package */static void submit(Runnable task, long delay, long period, TimeUnit unit) {
		if (task != null) {
			getThreadPool().scheduleAtFixedRate(task, delay, period, unit);
		}
	}

	public static ScheduledExecutorService getThreadPool() {
		if (sExecutor == null) {
			synchronized (sLock) {
				if (sExecutor == null) {
					sExecutor = Executors.newScheduledThreadPool(POOL_SIZE, sThreadFactory);
				}
			}
		}
		return sExecutor;
	}
}
